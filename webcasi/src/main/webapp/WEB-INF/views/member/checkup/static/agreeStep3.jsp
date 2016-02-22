<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/assets/css/common_static.css"/>	

	
<title>삼성병원 건강검진</title>
<script>
<!--
	$(document).ready(function(){
		var agreeType = "";
		
		$("input:radio").change(function(){
			if($(this).is(':checked')){
				var id = $(this).attr("id");
				
				if(id == "agreeFlag1Agree"){
					agreeType = "FIRST_AGREE";
				}else if(id=="agreeFlag1Disagree"){
					agreeType = "FIRST_DISAGREE";
				}else if(id=="agreeFlag2Agree"){
					agreeType = "SECOND_AGREE";
				}else{
					agreeType = "SECOND_DISAGREE";
				}
				
				var url = "/member/checkup/updateAgreeFlag?agreeFlag=" + agreeType;
				var params = {};
				
				$.ajax({
					dataType:  'json', 
					type : 'POST',
					url : _requestPath + url,
					timeout : 5000,
					data : params,
					beforeSubmit : function(){
						
					},				
					success : function(result){
						var success = result.success;
						if(success){
							
							
						}else{
							alert(result.msg);
						}
						
					},
					error : function(response, status, err){
						alert(err);
						
					}
				});	//Ajax로 호출한다.
			}
		});
		
		
		
	});
	
	
	
	
	function goNext(){
		var firstOK = false;
		var secondOK = false;
		$("input:radio").each(function(){
			if($(this).is(':checked')){
				var id = $(this).attr("id");
				if(id.startsWith("agreeFlag1")){
					firstOK = true;
				}
				if(id.startsWith("agreeFlag2")){
					secondOK = true;
				}
			}
		});
		if(firstOK && secondOK){
			document.location.href = "${pageContext.request.contextPath}/member/checkup/static/viewGuide";
		}else{
			alert("동의서에 동의함 또는 동의하지않음을 체크해주세요.");
			return;
		}
		
	}
	
	function clickRadioLink(id){
		var radioObj = $("#"+id);
		if(!radioObj.is(':checked') && !radioObj.is(':disabled')){
			radioObj.attr("checked",true);
			processRadioEvent(radioObj);
		}
	}
	
-->
</script>
</head>
<body>
<div class="agree-content">
       <div class="consent">
			<div class="consent">
				<div class="img03_1"><img src="${pageContext.request.contextPath}/resources/assets/images/consent3_01.png"></div>
				<div class="txt03_1">
					본 동의서는 2013년 3월 19일 강북삼성병원 임상시험심사위원회에서 승인을 받은 동의서 입니다. 
				</div>
				<div class="txt03_2">
					<div class="c_main">
						「강북삼성코호트연구」는 삼성의료원과 미국의 존스홉킨스 보건대학(Johns Hopkins Bloomberg
					School of Public Health)의 연구자들을 중심으로 한국인의 질병 발생 경과를 알아보고 만성질환, 
					심혈관계 질환 등의 예방과 치료에 도움이 되는 정보를 얻고자, 광범위한 인구집단의 질병과 건강 상태의
					자연 경과, 예후, 질병 결정 인자들을 평가하는 대규모의 장기 연구조사사업입니다.
					강북삼성코호트연구의 주 참여 대상은 연구 기간 동안 강북삼성병원 종합건진센터를 방문하시는
					만 19세 이상의 성인남녀 건강검진 수진자 분들입니다.<p><br>

					본 연구에 참여를 동의하시면, 귀하가 수행하신 건강 문진조사와 검진 결과의 정보를 연구 목적으로
					활용하게 됩니다. 또한 암 또는 만성질환의 발생과 사망 여부를 확인하기 위한 목적으로 공공 자료원
					(통계청 사망자료, 국민건강보험공단 수진자료, 건강보험심사평가원 청구자료, 국립암센터 중앙암등록
					자료 등)과도 연계하여 질병발생 여부를 확인하고자 합니다. 본 연구와 관련하여 수집된 귀하의 개인
					정보는 「공공기관의 개인정보보호에 관한 법률」등에 의거하여 관리되며 개인 식별이 불가능하도록
					연구목적으로 이용되는 코드를 별도로 생성하여 보관할 것이며, 특별하게 규정한 경우를 제외하고는
					개인정보에 접근할 수 없도록 할 것 입니다.<p><br>
					본 연구에 참여하여 연구목적으로 건강검진 자료 이용에 동의하신 이후라도 언제든지 귀하게서는
					동의 철회를 요청할 수 있으며 연구자는 그 요청을 전적으로 받아들여 자료 이용을 하지 않을 것입니다.
					또한, 동의 철회로 인하여 귀하에게 어떠한 불이익도 발생하지 않을 것 입니다.
					모든 자료 관리 및 연구에 관한 문의는 연구 담당자 혹은 임상시험심사위원회(또는 기관생명윤리위원
					회)에 하실 수 있습니다. 동의하신 경우, 요청하시면 동의서의 사본 1부를 교부 받아 보관할 수 있습니다.<p><br>

					<em>참여의 유익성 : </em><br/>
					귀하가 본 연구에 참여함으로써 경제적 보상이나 질병 치료에 대한 직접적인 이득은 없습니다.
					그러나 귀하의 참여는 만성질환 연구에 귀한 자료를 제공함으로써 향후 우리나라 국민들의 질병 예방
					및 조기발견에 큰 보탬이 될 수 있습니다.
					</div>
					<div class="c_contact2">연구 담당자 :<em>02-2001-5176, 5278</em></div>
					<div class="c_contact3">강북삼성병원 임상시험위원회 연락처 : 김 미 경<em>02-2001-1944</em></div>
				</div>
			
				<div class="txt03_3">
					※ 다음 각 항목에 대해 면접자의 설명을 들은 후 충분히 이해하였을 경우 동의 여부를 해당란에<br>
					표시하여 주십시오.
				</div>
				<div class="txt03_4">
					<div class="box1">
						1. 본인은 면접자로부터 설명서와 본 동의서의 내용에 대한 자세한 설명을 듣고
						그 내용을 잘 이해하였으며 자발적으로 이 연구에 참여하는 것에 동의합니다.
					</div>
					<div class="box2">
						<c:choose>
							<c:when test="${empty firstAgreed}">
								<li><input type="radio" id="agreeFlag1Agree" name="agreeFlag1"><a href="javascript:clickRadioLink('agreeFlag1Agree');">동의함</a></li>
								<li><input type="radio" id="agreeFlag1Disagree" name="agreeFlag1"><a href="javascript:clickRadioLink('agreeFlag1Disagree');">동의하지않음</a></li>
							</c:when>
							<c:when test="${firstAgreed eq true}">
								<li><input type="radio" id="agreeFlag1Agree" name="agreeFlag1" checked><a href="javascript:clickRadioLink('agreeFlag1Agree');">동의함</a></li>
								<li><input type="radio" id="agreeFlag1Disagree" name="agreeFlag1"><a href="javascript:clickRadioLink('agreeFlag1Disagree');">동의하지않음</a></li>
							</c:when>
							<c:otherwise>
								<li><input type="radio" id="agreeFlag1Agree" name="agreeFlag1" ><a href="javascript:clickRadioLink('agreeFlag1Agree');">동의함</a></li>
								<li><input type="radio" id="agreeFlag1Disagree" name="agreeFlag1" checked><a href="javascript:clickRadioLink('agreeFlag1Disagree');">동의하지않음</a></li>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class="txt03_5">
					<div class="box1">
						2. 본인의 연구의 목적으로 공공자료원 (통계청 사망자료, 국민건강보험공단
						수진자료, 건강보험심사평가원 청구자료, 국립암센터 중앙암등록자료 등)과
						연계하여 질병발생여부를 확인하는 데에 동의합니다.
					</div>
					<div class="box2">
						<c:choose>
							<c:when test="${empty firstAgreed}">
								<li><input type="radio" id="agreeFlag2Agree" name="agreeFlag2"><a href="javascript:clickRadioLink('agreeFlag2Agree');">동의함</a></li>
								<li><input type="radio" id="agreeFlag2Disagree" name="agreeFlag2"><a href="javascript:clickRadioLink('agreeFlag2Disagree');">동의하지않음</a></li>
							</c:when>
							<c:when test="${firstAgreed eq true}">
								<li><input type="radio" id="agreeFlag2Agree" name="agreeFlag2" checked><a href="javascript:clickRadioLink('agreeFlag2Agree');">동의함</a></li>
								<li><input type="radio" id="agreeFlag2Disagree" name="agreeFlag2"><a href="javascript:clickRadioLink('agreeFlag2Disagree');">동의하지않음</a></li>
							</c:when>
							<c:otherwise>
								<li><input type="radio" id="agreeFlag2Agree" name="agreeFlag2" ><a href="javascript:clickRadioLink('agreeFlag2Agree');">동의함</a></li>
								<li><input type="radio" id="agreeFlag2Disagree" name="agreeFlag2" checked><a href="javascript:clickRadioLink('agreeFlag2Disagree');">동의하지않음</a></li>
							</c:otherwise>
						</c:choose>

					</div>
				</div>
				
				<div class="txt03_3">
					⊙ 참여 동의서에 서명은 수진당일 접수실에서 하실 수 있습니다.
				</div>				
				
				<div class="txt03_7">KBC11070  Version 5.0, 2013.03.11</div>
			</div>				
			
			
		</div>
		<c:url value="${firstUrl}" var="nextUrl"/>
		<div class="home-action2">
			<div class="info-button"><a href="javascript:goNext();"><img src="${pageContext.request.contextPath}/resources/assets/images/bt_next_2.png"/></a></div>
		</div>
	</div>
	
</body>
</html>