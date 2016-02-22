$(document).ready(function(){
	
	$("input:checkbox").change(function(){
		var tabQuestion = $(this).attr("tabQuestion");
		if(tabQuestion == "true"){
			//탭질문 하위에 바로 존재하는 check일경우에는 이벤트를 무시한다.
			return;
		}
		var onOff = "";
		var questionItemId = $(this).attr("id");
		var questionId = $(this).attr("questionId");
		var itemType = $(this).attr("itemType");
		var existChild = $(this).attr("existChild");
		if($(this).is(':checked')){
			onOff = "on";
			
			if(existChild == "true"){
				showDepth(questionItemId);
			}
			
		}else{
			onOff = "off";
			
			if(existChild == "true"){
				hideDepth(questionItemId);
			}
		}
		
	});
	
	
	$("input:radio").change(function(){
		var itemType = $(this).attr("itemType");		
		var existChild = $(this).attr("existChild");
		var itemGroup = $(this).attr("itemGroup");
		
		
		
		if(itemType.startsWith("OBJ_")){
			//객관식일 경우 객관식 처리를 별도로 한다.
			submitObjective($(this),itemType,existChild);
			return;
		}
		
		if(itemType == "CHECK_SUBJ_RADIO_SUBJ"){
			//별도의 처리를 한다,
			submitSubjRadioSubj($(this),itemType,existChild);
			return;
		}
		
		if(itemType == "RADIO_RADIO"){
			submitRadioRadio($(this),itemType,existChild);
			return;
		}
		
		var questionItemId = $(this).attr("id");
		var questionId = $(this).attr("questionId");
		
		
		if($(this).is(':checked')){
			if(existChild == "true"){
				showDepth(questionItemId);
			}
		}else{
			if(existChild == "true"){
				hideDepth(questionItemId);
			}
		}
		
	});
	
	$("input:text").blur(function(){
		var tabQuestion = $(this).attr("tabQuestion");
		if(tabQuestion == "true"){
			//탭질문 하위에 바로 존재하는 text일경우에는 이벤트를 무시한다.
			return;
		}
		
		var questionId = $(this).attr("id");
		if(questionId == null || questionId == undefined){
			questionId = $(this).attr("questionId");
		}
		var questionType = $(this).attr("questionType");
		var validator = $(this).attr("validator");
		var itemType = $(this).attr("itemType");
		var existChild = $(this).attr("existChild");
		if(existChild == null || existChild == undefined){
			existChild = false;
		}
		
		if(questionType == "SUBJECTIVE_YEAR_MONTH" || questionType == "SUBJECTIVE_YEAR_MONTH_DAY"){
			return;
		}
		
		var value = $(this).val();
		if(value == null){
			return;
		}
		
		if(!doValidation(value,validator)){
			$(this).val("");
			return;
		}
		
		if(itemType == "OBJ_RADIO_SUBJ" || itemType == "CHECK_SUBJ" || itemType == "CHECK_SUBJ_SUBJ" 
			|| itemType == "CHECK_SUBJ_1" || itemType == "CHECK_SUBJ_RADIO_SUBJ" || itemType == "RADIO_SUBJ_HOUR_MINUTE" 
			|| itemType == "RADIO_SUBJ_1"){
			//아이템에 붙어있는 text일 경우에 별도로 처리한다.
			submitItemSubjective($(this),itemType,existChild);
			return;
		}
		
	});
	
	$("textarea").blur(function(){
		var questionId = $(this).attr("id");
		var questionType = $(this).attr("questionType");

		var itemType = $(this).attr("itemType");
		
		var value = $(this).val();
		if(value == null){
			return;
		}
	
	});
	
	
	
	$(".SUBJECTIVE_YEAR_MONTH").monthpicker({
		showOn : 'both',
		changeYear : true,
		dateFormat : 'yy-MM',
		buttonImage:  _requestPath + "/resources/images/calendar.png",
		buttonImageOnly: true,
		monthNames : ['01','02','03','04','05','06','07','08','09','10','11','12'],
		monthNamesShort : ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
		onSelect : function(dateText){
			var dateObj = $(this);
			//submitSubjective(dateObj,dateText);
		}
	});
	
	$(".SUBJECTIVE_YEAR_MONTH_DAY").datepicker({
		showOn : 'both',
		dateFormat : 'yy-MM-dd',
		buttonImage:  _requestPath + "/resources/images/calendar.png",
		buttonImageOnly: true,
		changeYear : true,
		changeMonth : true,
		dayNamesMin : ['월','화','수','목','금','토','일'],
		monthNames : ['01','02','03','04','05','06','07','08','09','10','11','12'],
		monthNamesShort : ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
		onSelect : function(dateText){
			var dateObj = $(this);
			//submitSubjective(dateObj,dateText);
		}		
	});
	
	
});


function submitSubjective($dateObj,value){
	var questionId = $dateObj.attr("id");
	var questionType = $dateObj.attr("questionType");
	

}

/**
 * Objective유형의 답변을 submit을 한다.
 * @param $obj
 */
function submitObjective($obj,itemType,existChild){
	var questionId = $obj.attr("questionId");
	var questionItemId = $obj.attr("itemId");
	var embededItemId = $obj.attr("embededItemId");
	

}

/**
 * RADIO_RADIO에 대한 처리를 한다.
 * @param $obj
 * @param itemType
 * @param existChild
 */
function submitRadioRadio($obj,itemType,existChild){
	var questionId = $obj.attr("questionId");
	var questionItemId = $obj.attr("itemId");
	var embededItemId = $obj.attr("embededItemId");
	
	
}

/**
 * check_subj_radio_subj일 경우에 radio값을 처리한다.
 * @param $obj
 */
function submitSubjRadioSubj($obj,itemType,existChild){
	var questionId = $obj.attr("questionId");
	var questionItemId = $obj.attr("itemId");
	var embededItemId = $obj.attr("embededItemId");

}

function submitItemSubjective($obj,itemType,existChild){
	var questionId = $obj.attr("questionId");
	var questionItemId = $obj.attr("itemId");
	
	var value = $obj.val();
	

}


//주관식에서 입력한 값이 유효한지 검사한다.
function doValidation(value,validator){
	if(validator == "NONE"){
		return true;
	}
	
	if(validator == "NUMBER" || validator == "NUMBER_0_9" || validator == "AGE" ||
		validator == "HOUR" || validator == "MINUTE" || validator == "YEAR" || validator == "MONTH"){
		//숫자인지 검사를 먼저한다.
		if(isNaN(value)){
			alert("숫자만 입력가능합니다.");
			return false;
		}
		if(validator == "NUMBER_0_9"){
			if(value < 0 || value > 9){
				alert("0~9까지만 입력가능합니다.");
				return false;
			}
		}
		if(validator == "HOUR"){
			if(value < 0 || value > 24){
				alert("0~24까지만 입력가능합니다.");
				return false;
			}
		}
		if(validator == "MINUTE"){
			if(value < 0 || value > 60){
				alert("0~60까지만 입력가능합니다.");
				return false;
			}
		}		
		if(validator == "MONTH"){
			if(value < 0 || value > 12){
				alert("0~12까지만 입력가능합니다.");
				return false;
			}
		}		
	}
	
	return true;
}

function wrapWindowByMask(){
    //화면의 높이와 너비를 구한다.
    var maskHeight = $(document).height();  
    var maskWidth = $(window).width();  

    //마스크의 높이와 너비를 화면 것으로 만들어 전체 화면을 채운다.
    $('#mask').css({'width':maskWidth,'height':maskHeight});  

    //애니메이션 효과
    $('#mask').show();      

}

function hideMask(){
	$('#mask, .window').hide(); 
}