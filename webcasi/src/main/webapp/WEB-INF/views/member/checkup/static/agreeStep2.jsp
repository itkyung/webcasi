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
</head>
<body>
<div class="agree-content">
       <div class="consent">
			<div class="consent">
				<div class="img01"><img src="${pageContext.request.contextPath}/resources/assets/images/consent2_02.png"></div>
				<div class="txt01">
					<em>'강북삼성코호트연구'</em> 참여로 여러분이 정기적으로 받고 계신<br/>
					건강진단을 보다 알차게 만드시기 바랍니다.
				</div>
				<div class="txt02">강북삼성병원 종합검진 수진자 여러분 안녕하십니까?</div>
				<div class="txt03">
					최근 발생이 급증하고 있는 대부분의 만성질환들이 여러분의 생활양식 및 식생활습관 등과 관계가 있다는 것을 알고 계십니까?
				</div>
				<div class="txt04">
					미국, 영국 등 선진국에서는 이러한 질병의 원인과 의료산업의 발전을 위하여 1950년대부터 대규모 코호트 연구(특정 인구집단의 건강과 질병 발생 여부를 장기간 추적 관찰해 원인을 찾아내는 연구)를 실시해 왔습니다.<br/>
					하지만 이 연구의 결과들은 한국인의 특성에 맞지 않아, 한국인에게 있어서 구체적인 질병의 원인과 예방을 규명하기 위한 연구가 필요한 상황입니다.
				</div>
				<div class="txt03">
					저희 강북삼성병원 종합건진센터에서는 현대인에게 급증하고 있는 만성질환의 생활습관적, 유전적, 환경적 발병 위험요인을 밝히고 수진자 여러분의 건강을 관리 하기 위하여 ‘강북삼성코호트연구’를 수행하고 있습니다.
				</div>
				<div class="txt05">
					<div class="left">
						강북삼성코호트연구는 세계최고의 의료 기관인 미국의 존스<br>
						홉킨스 대학과 공동으로 진행하고 있으며, 2011년부터 2015년<br>
						사이 강북삼성병원 종합건진센터의 건강 검진에 참여한 30만명<br>
						의 수진자들을 대상으로 이루어집니다.
					</div>
					<div class="right">
						<img src="${pageContext.request.contextPath}/resources/assets/images/consent2_01.png">
					</div>
				</div>
				<div class="txt03">
					구체적으로 귀하가 수행하신 문진표와 검진자료를 장기간 추적 관찰하여 특정 질병과 그 원인을 밝히고자 합니다.
				</div>
				<div class="txt04">
					귀하께서 연구를 참여하시는데 드는 별도의 시간과 비용은 없으며, 연구에 참여하실 경우 정기적으로 발행되는
					건강관리안내 책자를 보내 드립니다. 뒷면에 동의서를 읽어보시고, 참여를 원하실 경우 첨부된 동의서에 바로
					서명하시거나 검진을 하러 오시는 날 센터에서 동의서를 작성해 주시길 부탁드립니다
				</div>
				<div class="txt07">감사합니다.</div>
				<div class="txt06">
					<div class="right">강북삼성병원장 </div>
				</div>
			</div>		
		</div>
		<c:url value="${firstUrl}" var="nextUrl"/>
		<div class="home-action2">
			<div class="info-button"><a href="${pageContext.request.contextPath}/member/checkup/static/agreeStep3"><img src="${pageContext.request.contextPath}/resources/assets/images/page_bt_next.png"/></a></div>
		</div>
	</div>
	
</body>
</html>