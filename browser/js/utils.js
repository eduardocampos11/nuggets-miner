toggleComments = function(id){
	$(id).slideToggle('slow');
}



//HTML Pre-processing
function processHTML(){
	$('code').each(function(index){
		var content = $(this).text();
		var lines = content.match(/[^\r\n]+/g).length;
		
		if(lines > 1)
			$(this).addClass('prettyprint linenums');
		else
			$(this).addClass('prettyprint');
	});

	prettyPrint();
	
}