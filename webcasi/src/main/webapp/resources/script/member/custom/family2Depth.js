function completed(goPre){
	if(goPre){
		var isRadio = _type.startsWith("RADIO") ? true :false;
		
		if(confirm("입력한 사항을 취소하시겠습니까?")){
			parent.complete2Depth(_itemId,_questionId,isRadio,true);
		}
		
		return;
	}
	
	var canGoNext = true;
	$("input:hidden").each(function(){
		var id = $(this).attr("id");
		if(id.startsWith("complete_flag_")){
			var val = $(this).val();
			if(val == "false"){
				canGoNext = false;
			}
		}
	});
	
	if(canGoNext){
		var isRadio = true;
		parent.complete2Depth(_itemId,_questionId,isRadio);
	}else{
		alert("필수 질문에 답변이 필요합니다.");
	}
}


$(document).ready(function(){
	$('#family-contents').jScrollPane();
	
	
	
});


function _updateProgress(){
	//dummy
}
