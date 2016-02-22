var _processingFlag = false;

$(document).ready(function(){
	
	$("input:checkbox").each(function(){
		var nurseCheck = $(this).attr("nurseCheck");
		if(nurseCheck != true){
			$(this).attr('disabled','disabled');
		}
	});
	
	$("input:checkbox").change(function(){
		alert("수정이 불가능합니다.값을 바꾸어도 실제로 수정되어서 적용이 되질 않습니다.");
		if($(this).is(':checked')){
			$(this).attr("checked",false);
		}else{
			$(this).attr("checked",true);
		}
		
	});
	
	
	$("input:radio").change(function(){
		alert("수정이 불가능합니다.값을 바꾸어도 실제로 수정되어서 적용이 되질 않습니다.");
	});
	
	$("input:radio").each(function(){
		var nurseCheck = $(this).attr("nurseCheck");
		if(nurseCheck != true){
			$(this).attr('disabled','disabled');
		}
	});
	
	$("input:text").each(function(){
		var nurseCheck = $(this).attr("nurseCheck");
		if(nurseCheck != true){
			$(this).attr('disabled','disabled');
		}
	});
	
	$("input:text").blur(function(){
		alert("수정이 불가능합니다.값을 바꾸어도 실제로 수정되어서 적용이 되질 않습니다.");
	});
	
	$("textarea").blur(function(){
		alert("수정이 불가능합니다.값을 바꾸어도 실제로 수정되어서 적용이 되질 않습니다.");
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
		dayNamesMin : ['일','월','화','수','목','금','토'],
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