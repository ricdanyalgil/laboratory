var diretivas = angular.module('catalogo.directives', []);

diretivas
		.directive(
				'ngAnaliseSituacao',
				function() {

					return {
						// restrict: 'C',
						// require: '^ngSituacao',
						scope : {
							situacao : '@'
						},
						template : '<span class="label"><i class="fa"> </i> {{situacao}}</span>',

						link : function(scope, elem, $attrs) {

							$attrs.$observe('situacao', function(situacao) {
								var labelType = 'label-primary';
								var icon = 'fa-pencil-square-o';
								if (situacao == 'Aprovado') {
									var labelType = 'label-success';
									var icon = 'fa-thumbs-o-up';
								} else if (situacao == 'Reprovado') {
									var labelType = 'label-danger';
									var icon = 'fa-thumbs-o-down';
								}
								$(elem).children().first().addClass(labelType)
										.children().first().addClass(icon);
							});

						}
					};
				});

diretivas
		.directive(
				'ngAnaliseSituacaoButton',
				function() {

					return {
						// restrict: 'C',
						// require: '^ngSituacao',
						scope : {
							situacao : '@'
						},
						transclude : true,
						template : '<a data-toggle="dropdown" class="btn btn-sm  dropdown-toggle">'
								+ '<i class="fa"></i> {{situacao}} <span class="caret"></span>'
								+ '</a>'
								+ '<ul class="dropdown-menu dropdown" ng-transclude>'
								+ '</ul>',

						link : function(scope, elem, $attrs) {

							$attrs
									.$observe(
											'situacao',
											function(situacao) {
												var btnClasses = 'btn-primary btn-success btn-danger';
												var iconClasses = 'fa-pencil-square-o fa-thumbs-o-up fa-thumbs-o-down';
												var btnType = 'btn-primary';
												var icon = 'fa-pencil-square-o';
												if (situacao == 'Aprovado') {
													var btnType = 'btn-success';
													var icon = 'fa-thumbs-o-up';
												} else if (situacao == 'Reprovado') {
													var btnType = 'btn-danger';
													var icon = 'fa-thumbs-o-down';
												}
												$(elem)
														.children()
														.first()
														.removeClass(btnClasses)
														.addClass(btnType)
														.children().first()
														.removeClass(
																iconClasses)
														.addClass(icon);
											});

						}
					};
				});

diretivas.directive('ngAnexos', function() {
	return {
		restrict : 'E',
		require : '^ngModel',
		scope : {
			ngModel : '='
		},
		templateUrl : 'directives/anexo.html',
		link : function(scope, elem, $attrs) {
		}
	};
});

diretivas.directive('ngCampoUsuario', function() {
	return {
		restrict : 'E',
		require : '^ngModel',
		scope : {
			usuario : '=ngModel',
			fase: '@',
			name: '@'
		},
		templateUrl : 'directives/campo-usuario.html',
		link : function(scope, elem, $attrs) {
			
		},
		controller: function ($scope, $http, AlertService) {
			$scope.palavraChave = "";
			$scope.resultadoPesquisa = [];			
				
			$scope.selecionar = function(m){		
				$http({
					url : 'api/fase/usuario/carregar',
					method : "POST",
					data : m,
					headers : {
						'Content-Type' : 'application/json;charset=utf8'
					}
				}).success(function(data) {
					$scope.usuario = data;	
					delete $scope.usuario.grupos;					
				}).error( function(data, status) {
					AlertService.addWithTimeout('danger',data[0].message);
				});
				
			};				
			
			$scope.pesquisar = function(){
				if(!$scope.palavraChave || $scope.palavraChave.length<3){
					AlertService.addWithTimeout('warning', "Para pesquisar digite pelo menos 3 letras.");
				}else{
					$http.get('api/user/nome/' + $scope.palavraChave).success(function(data) {
						if (data == "") {
							AlertService.addWithTimeout('warning', 'Usuário não encontrado no LDAP');
						}else{
							$scope.resultadoPesquisa = data;
						}
					}).error(function(data, status) {
						if (status == 412) {
							AlertService.addWithTimeout('danger', data[0].message);
						}
					});
				}
			};
		}
	}
});

diretivas.directive('ngHistoricoFase', function() {
	return {
		restrict : 'E',
		require : '^ngModel',
		scope : {
			fase : '=ngModel'
		},
		templateUrl : 'directives/historico-fase.html',
		link : function(scope, elem, $attrs) {
			
		},
		controller: function ($scope, $http, AlertService) {
			$scope.historico = [];			
				
			$http({
				url : 'api/fase/historico/'+$scope.fase.id,
				method : "GET"
			}).success(function(data) {
				$scope.historico = data;						
			}).error( function(data, status) {
				AlertService.addWithTimeout('danger',data[0].message);
			});
		}
	};
});

diretivas.directive('ngObservacoes', function() {
	return {
		restrict : 'E',
		require : '^ngModel',
		scope : {
			ngModel : '='
		},
		templateUrl : 'directives/observacoes.html',
		link : function(scope, elem, $attrs) {
		}
	};
});

diretivas.directive('ngAlerts', function() {
	return {
		restrict : 'E',
		templateUrl : 'directives/alerts.html'
	};
});

diretivas.directive('ngClickConfirm', function() {
	return {
		restrict : 'A',
		link : function(scope, elt, attrs) {
			elt.bind('click', function(e) {
				var message = attrs.msg || "Você tem certeza?";
				bootbox.confirm(message, function(result) {
					if (result) {
						var action = attrs.ngClickConfirm;
						scope.$eval(action);
					}
				});
			});
		},
	};
});

diretivas.directive('backButton', function() {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			element.bind('click', function() {
				history.back();
				scope.$apply();
			});
		}
	};
});

diretivas.directive('ngMembros', function() {
	return {
		restrict : 'E',
		require : '^ngModel',
		scope : {
			ngModel : '='
		},
		templateUrl : 'directives/membros.html',
		link : function(scope, elem, $attrs) {
		}
	};
});

diretivas.directive('ngInteressados', function() {
	return {
		restrict : 'E',
		require : '^ngModel',
		scope : {
			ngModel : '='
		},
		templateUrl : 'directives/interessados.html',
		link : function(scope, elem, $attrs) {
		}
	};
});

diretivas.directive('ngProdutos', function() {
	return {
		restrict : 'E',
		require : '^ngModel',
		scope : {
			ngModel : '='
		},
		templateUrl : 'directives/produtos-da-fase.html',
		link : function(scope, elem, $attrs) {
		}
	};
});

diretivas.directive('ngFluxo', function() {
	return {
		restrict : 'E',
		require : '^ngModel',
		scope : {
			ngModel : '='
		},
		templateUrl : 'directives/fluxo.html',
		link : function(scope, elem, $attrs) {
		}
	};
});

diretivas.directive('donut', function() {
	return {
		restrict : 'A',
		scope : {
			donutData : '=',
			donutColors : '='
		},
		link : function(scope, element, attrs) {
			var config = {
				element : element,
				data : scope.donutData,
				resize : true
			};
			if (scope.donutColors) {
				config.colors = scope.donutColors;
			}

			Morris.Donut(config);
		}
	};
});

diretivas.directive('validationMsg', function(ValidationService) {
	return {
		restrict : 'E',
		scope : {
			propriedade : '@'
		},
		template : "<div class='error text-danger' ng-show='msg'><small class='error' >{{msg}}</small></div>",		
		controller : function($scope) {
			$scope.$watch(function() {
				return ValidationService.validation[$scope.propriedade];
			}, function(msg) {
				$scope.msg = msg;
			});
		}
	};
});