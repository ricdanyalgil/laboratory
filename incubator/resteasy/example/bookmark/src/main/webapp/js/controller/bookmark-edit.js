$(function() {
    $(document).ready(function() {
	if (id = $.url().param('id')) {
	    BookmarkProxy.load(id, loadOk, loadFailed);
	}
    });

    $("form").submit(function(event) {
	event.preventDefault();
    });

    $("#save").click(function() {
	var form = {
	    description : $("#description").val(),
	    link : $("#link").val()
	};

	if (id = $("#id").val()) {
	    BookmarkProxy.update(id, form, saveOk, saveFailed);
	} else {
	    BookmarkProxy.insert(form, saveOk, saveFailed);
	}
    });
});

function loadOk(data) {
    $("#id-row").show();
    $("#id-text").html(data.id);
    $("#id").val(data.id);
    $("#description").val(data.description);
    $("#link").val(data.link);
}

function loadFailed(request) {
    switch (request.status) {
    case 404:
	console.log(request);
	break;

    default:
	break;
    }
}

function saveOk(data) {
    location.href = "bookmark-list.html";
}

function saveFailed(request) {
    switch (request.status) {
    case 412:
	$($("form input").get().reverse()).each(function() {
	    var id = $(this).attr('id');
	    var message = null;

	    $.each(request.responseJSON, function(index, value) {
		if (id == value.property) {
		    message = value.message;
		    return;
		}
	    });

	    if (message) {
		$("#" + id + "-message").html(message).show();
		$(this).focus();
	    } else {
		$("#" + id + "-message").hide();
	    }
	});
	break;

    default:
	break;
    }
}