jQuery(document).ready(function(){
	
	$("#progress-wrapper").masonry({
		itemSelector : '.progress-entity',
		isAnimated: true,
		animationOptions: {
		    duration: 400
		  }
	});
	
});

function goGroup(id){
	
	parent._goDirectGroup(id);
}