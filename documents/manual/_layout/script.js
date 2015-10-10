$(document).ready(function(){
	generateContents();
	writeReferences();
	positionNavigation();
	
	$(".fancybox").fancybox();
});

$(window).load(function () {
	setIframeHeight(document.getElementById('ucif'));
});
/*window.onload = function () {
			setIframeHeight(document.getElementById('ucif'));
		};*/

$(window).resize(function() {
	positionNavigation();
});
 
function generateContents() {
	h1iter = 0;
	h2iter = 0;
	h3iter = 0;
	h4iter = 0;

	$('h1[id], h2[id], h3[id], h4[id]').each(function() {
		
		if($(this).is('h1')) {
			h1iter++;
			h2iter = h3iter = h4iter = 0;
			
			clazz = 'h1-title';
			numberator = h1iter;
			
		} else if($(this).is('h2')) {
			h2iter++;
			h3iter = h4iter = 0;
			
			clazz = 'h2-title';
			numberator = h1iter + '.' + h2iter;
			
		} else if($(this).is('h3')) {
			h3iter++;
			h4iter = 0;
			
			clazz = 'h3-title';
			numberator = h1iter + '.' + h2iter + '.' + h3iter;
			
		} else if($(this).is('h4')) {
			h4iter++;
			
			clazz = 'h4-title';
			numberator = h1iter + '.' + h2iter + '.' +  h3iter + '.' + h4iter;
		}
		
        $(this).addClass('haslink');
        $(this).append(
            '<a class="anchor" href="#' + $(this).attr("id") + '"></a>'
        );
		 $(this).prepend('<span class="numberator">' + numberator + '</span> ');
		
		
		ref = $('nav ul').append('<li class="' + clazz + '"><a href="#' + $(this).attr("id") + '">' + $(this).html() + '</a></li>');
		ref.children().children().filter('.anchor').remove();
    });
}


function writeReferences() {
	$('.reference').each(function() {
		$(this).html('<a href="#' + $($(this).attr("title")).attr("id") + '">' + $($(this).attr("title")).html() + '</a>');
		$(this).children().filter('.anchor').remove();
	});
	
	$('.reference.noIndex').each(function() {
		$(this).html('<a href="#' + $($(this).attr("title")).attr("id") + '">' + $($(this).attr("title")).html() + '</a>');
		$(this).children().filter('.anchor').remove();
		$(this).children().children().filter('.numberator').remove();
	});
	
	$('.reference').each(function() {
		$(this).attr("title", "Springe zum Absatz");
	});
}

function positionNavigation() {
	if($("body").width() < 1180) {
		$("nav").addClass("innerNavigation");
		$("#wrapper").addClass("innerNavigation");
	} else {
		$("nav").removeClass("innerNavigation");
		$("#wrapper").removeClass("innerNavigation");
	}
}

function setIframeHeight(iframe) {
	if (iframe) {
		var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
		if (iframeWin.document.body) {
			iframe.height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
		}
	}
};