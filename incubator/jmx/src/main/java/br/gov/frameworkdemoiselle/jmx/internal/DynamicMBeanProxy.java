package br.gov.frameworkdemoiselle.jmx.internal;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map.Entry;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;

import br.gov.frameworkdemoiselle.DemoiselleException;
import br.gov.frameworkdemoiselle.internal.producer.ResourceBundleProducer;
import br.gov.frameworkdemoiselle.management.annotation.Managed;
import br.gov.frameworkdemoiselle.management.internal.ManagedType;
import br.gov.frameworkdemoiselle.management.internal.ManagedType.FieldDetail;
import br.gov.frameworkdemoiselle.management.internal.ManagedType.MethodDetail;
import br.gov.frameworkdemoiselle.management.internal.ManagedType.ParameterDetail;
import br.gov.frameworkdemoiselle.management.internal.MonitoringManager;
import br.gov.frameworkdemoiselle.util.Beans;
import br.gov.frameworkdemoiselle.util.ResourceBundle;

/**
 * <p>
 * This class is a MBean that gets registered everytime you mark a class with {@link Managed}. It dynamicaly reads the
 * fields and operations contained in a {@link Managed} class and exposes them to the MBean server. Everytime a client
 * tries to call an operation or read/write a property inside a Managed class, this class will call the appropriate
 * method and pass the result to the MBean client.
 * </p>
 * 
 * @author SERPRO
 */
public class DynamicMBeanProxy implements DynamicMBean {

	private MBeanInfo delegateInfo;
	
	private ManagedType managedType;

	private ResourceBundle bundle = ResourceBundleProducer.create("demoiselle-jmx-bundle", Locale.getDefault());

	public DynamicMBeanProxy(ManagedType type) {
		if (type == null) {
			throw new NullPointerException(bundle.getString("mbean-null-type-defined"));
		}
		managedType = type;
	}

	@Override
	public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
		// Se o bean ainda não foi lido para determinar seus atributos, o faz agora.
		if (delegateInfo == null) {
			initializeMBeanInfo();
		}

		MonitoringManager manager = Beans.getReference(MonitoringManager.class);
		return manager.getProperty(managedType, attribute);
	}

	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {

		// Se o bean ainda não foi lido para determinar seus atributos, o faz agora.
		if (delegateInfo == null) {
			initializeMBeanInfo();
		}

		MonitoringManager manager = Beans.getReference(MonitoringManager.class);
		manager.setProperty(managedType, attribute.getName(), attribute.getValue());
	}

	@Override
	public AttributeList getAttributes(String[] attributes) {
		if (attributes != null) {
			AttributeList list = new AttributeList();
			for (String attribute : attributes) {
				try {
					Object value = getAttribute(attribute);
					list.add(new Attribute(attribute, value));
				} catch (Throwable t) {
				}
			}

			return list;
		}

		return null;
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		AttributeList settedAttributes = new AttributeList();
		if (attributes != null) {
			for (Attribute attribute : attributes.asList()) {
				try {
					setAttribute(attribute);

					// A razão para separarmos a criação do atributo de sua adição na lista é que
					// caso a obtenção do novo valor do atributo dispare uma exceção então o atributo não será
					// adicionado na lista de atributos que foram afetados.
					Attribute attributeWithNewValue = new Attribute(attribute.getName(),
							getAttribute(attribute.getName()));
					settedAttributes.add(attributeWithNewValue);
				} catch (Throwable t) {
				}
			}
		}

		return settedAttributes;
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException,
			ReflectionException {

		// Se o bean ainda não foi lido para determinar seus atributos, o faz agora.
		if (this.delegateInfo == null) {
			initializeMBeanInfo();
		}

		MonitoringManager manager = Beans.getReference(MonitoringManager.class);
		return manager.invoke(managedType, actionName, params);
	}

	/**
	 * Initialize the Managed information for this instance of Managed
	 */
	private void initializeMBeanInfo() {
		// Aqui vamos armazenar nossos atributos
		ArrayList<MBeanAttributeInfo> attributes = new ArrayList<MBeanAttributeInfo>();

		// Aqui vamos armazenar nossas operações
		ArrayList<MBeanOperationInfo> operations = new ArrayList<MBeanOperationInfo>();

		// Oterndo fields com seus respectivos acessores
		for (Entry<String, FieldDetail> fieldEntry : managedType.getFields().entrySet()) {

			try {

				MBeanAttributeInfo attributeInfo = new MBeanAttributeInfo(fieldEntry.getKey(), fieldEntry.getValue()
						.getDescription(), fieldEntry.getValue().getGetterMethod(), fieldEntry.getValue()
						.getSetterMethod());
				attributes.add(attributeInfo);

			} catch (javax.management.IntrospectionException e) {
				throw new DemoiselleException(bundle.getString("mbean-introspection-error", managedType.getType()
						.getSimpleName()));
			}
		}

		// Para cada metodo verifica se ele está anotado com Operation e cria um MBeanOperationInfo para ele.
		for (Entry<String, MethodDetail> methodEntry : managedType.getOperationMethods().entrySet()) {

			MethodDetail methodDetail = methodEntry.getValue();

			ParameterDetail[] parameterTypes = methodDetail.getParameterTypers();

			MBeanParameterInfo[] parameters = parameterTypes.length > 0 ? new MBeanParameterInfo[parameterTypes.length]
					: null;

			if (parameters != null) {

				for (int i = 0; i < parameterTypes.length; i++) {

					parameters[i] = new MBeanParameterInfo(parameterTypes[i].getParameterName(), parameterTypes[i]
							.getParameterType().getCanonicalName(), parameterTypes[i].getParameterDescription());
				}
			}

			// Com todas as informações, criamos nossa instância de MBeanOperationInfo e
			// acrescentamos na lista de todas as operações.
			MBeanOperationInfo operation = new MBeanOperationInfo(methodDetail.getMethod().getName(),
					methodDetail.getDescription(), parameters, methodDetail.getMethod().getReturnType().getName(),
					MBeanOperationInfo.ACTION_INFO);

			operations.add(operation);

		}

		// Por fim criamos nosso bean info.
		delegateInfo = new MBeanInfo(managedType.getType().getCanonicalName(), managedType.getDescription(),
				attributes.toArray(new MBeanAttributeInfo[0]), null, operations.toArray(new MBeanOperationInfo[0]),
				null);

	}

	@Override
	public MBeanInfo getMBeanInfo() {
		if (delegateInfo == null) {
			initializeMBeanInfo();
		}

		return delegateInfo;
	}

}
