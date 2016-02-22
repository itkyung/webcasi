<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<c:url value="/an/home" var="mainUrl"/>
<c:url value="/guide/viewGuide" var="guideUrl"/>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<script>
		$(document).ready(function(){
			
			<c:if test="${not empty loginFailure}">
				setTimeout(function(){
					alert("비밀번호가 일치하지 않습니다.\n로그인문의하기 \n 서울센터 02)2001-1140 \n 수원센터 031)303-0300");
				},500);
				
			</c:if>
			
			
			
		});
		
		function viewGuide(){
			$.fancybox.open({
				href : "${guideUrl}",type : 'iframe', 
				openEffect : 'none',closeEffect : 'none', width : 1110, height :700,fitToView : false, autoSize	: false,
				helpers : {
					title : {type : 'float'},
					iframe : {
						scrolling : 'auto',
						preload   : true
					}
				},
				
			});
			
		}
		
		function wrapWindowByMask(){
			//$.fancybox.helpers.overlay.update();
			
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
		
		function hideLogin(){
			hideMask();
			$("#login-popup-wrapper").hide();
		}
		
		function login(){
			wrapWindowByMask();
			$("#login-popup-wrapper").show();
			 /*  $.fancybox.open({
				href : "#login-popup",type : 'inline', modal : 'true',
				openEffect : 'elastic',closeEffect : 'elastic', width : 400, height :310,
				helpers : {
					title : {type : 'float'}
					
				}
			});   */
			/*
			$("#name").blur(function(){
				checkUser();
			});
			
			$("#patno").blur(function(){
				checkUser();
			});
			*/
			
			$("#patno").keypress(function(e){
				var code = (e.keyCode?e.keyCode:e.which);
				if(code == 13){
					
					checkUser();
					e.preventDefault();
				}
			});
			
			/* $("#resno2").blur(function(){
				checkUser();
			});
			 */
			$("#j_password").keypress(function(e){
				var code = (e.keyCode?e.keyCode:e.which);
				if(code == 13){
					_login();
					e.preventDefault();
				}
			});
		}
		
		function findPatno(){
			
			hideLogin();
			
			$("#resno1").val("");
			$("#resno2").val("");
			
			$.fancybox.open({
				href : "#patno-popup",type : 'inline', 
				openEffect : 'elastic',closeEffect : 'elastic', width : 350, height :150,
				helpers : {
					title : {type : 'float'}
				}
			});
			
			$("#resno1").keyup(function(e){
				var val = $(this).val();
				if(val.length == 6){
					$("#resno2").focus();
				}
			});
			
		}
	
		function viewAgree(){
			
			$("#agree-desc").show();
		}
		
		function closeAgree(){
			$("#agree-desc").hide();
		}
		
	</script>
	
</head>
<body>
<div id="mask"></div>
<div>
<center>

	<c:choose>
		<c:when test="${_isMobile eq true}">
			<map name="002">
				<area shape="rect" coords="890,280,1136,632" href="javascript:viewGuide();">
				<area shape="rect" coords="890,1,1136,100" href="javascript:login();">
			</map>
			<img src="${pageContext.request.contextPath}/resources/images/intro_new.jpg" usemap="#002"/>
		</c:when>
		<c:otherwise>
			<map name="001">
				<area shape="rect" coords="900,320,1160,670" href="javascript:viewGuide();">
				<area shape="rect" coords="900,1,1160,100" href="javascript:login();">
			</map>
			<img src="${pageContext.request.contextPath}/resources/images/intro_new_big.jpg" usemap="#001"/>
		</c:otherwise>
	</c:choose>

</center>
</div>
<div id="login-popup-wrapper">
	<div id="login-popup">
		<c:url value="/loginAction" var="loginUrl"/>
		<form id="loginForm" action="${loginUrl}" method="POST">
			<input type=hidden id="j_username" name="j_username"/>
			<div class="login-form-id">
				<div class="login-form-inner1">
					<input type=text id="name" name="name" size="12" maxlength="12" onBlur="checkUser();">
				</div>	
					
						<img src="${pageContext.request.contextPath}/resources/images/login_bt1.png" onclick="javascript:findPatno();"/>
					
			</div>
			<div class="login-form-name">
				<div class="login-form-inner3">
					<input type=text id="patno" name="patno" size="12" maxlength="12" onBlur="checkUser();">
				</div>	
			</div>
			<div class="login-form-password">
				<div class="login-form-inner2">
					<input type=password id="j_password" name="j_password" size="13" disabled/>
				</div>
				<a href="javascript:_login();"><img src="${pageContext.request.contextPath}/resources/images/login_bt2.png"/></a>
			</div>
			
			<div class="login-form-bottom">
				<img src="${pageContext.request.contextPath}/resources/images/loginpage_text.png"/>
			</div>
			<div class="login-info">
				<span class="text-indent2"> * 건진예약 2주 전부터 스마트문진을 시작할 수 있습니다.</span>
				<img src="${pageContext.request.contextPath}/resources/images/icon_call.png" class="textmiddle"/><span class="text-indent">문의 : 서울센터 02)2001-1140 / 수원센터 031)303-0300</span>
			</div>
		</form>
	</div>
</div>
<div id="password-popup">
	<form id="passwordSetting" method="POST">
	<div class="password-input">
		<div class="password-form-inner1">
			<input type="password" id="nPassword" name="nPassword"/>
		</div>
	</div>
	<div class="password-input-2">
		<div class="password-form-inner2">
			<input type="password" id="rPassword" name="rPassword"/>
		</div>
	</div>
	<div class="password-btn">
		<a href="javascript:submitPassword();"><img src="${pageContext.request.contextPath}/resources/images/pw_bt.png"/></a>
	</div>
	<div class="password-phone">
			<img src="${pageContext.request.contextPath}/resources/images/icon_call.png" class="textmiddle"/><span class="text-indent">서울센터 02)2001-1140 / 수원센터 031)303-0300</span>
	</div>
	<div class="password-desc">
		----------------------------------------------------<br>
		비밀번호는 숫자 4자리로 만들어주시기 바랍니다.<br>
		생성된 비밀번호는 당해 년도 건진용으로만 사용<br>
		가능하며 다음 건진때 새롭게 설정할 수 있습니다.
	</div>
	</form>
</div>
<div id="patno-popup">
	<form id="patnoSearch" method="POST">
		
		<div class="patno-resno">
			<div class="patno-form-inner">
				<input type=text id="resno1" name="resno1" size="6" maxlength="6">
				-
				<input type=password id="resno2" name="resno2" size="7" maxlength="7">
			
				
			
			</div>
			<a href="javascript:searchPatno();"><img src="${pageContext.request.contextPath}/resources/images/id_search_bt.png"/></a>
		</div>
		<div class="patno-agree">
			<div class="patno-right">
				<input type="checkbox" id="agree" name="agree"/>개인정보이용동의<span style="display:inline-block;margin-left:5px;"><a href="javascript:viewAgree();">
				<img src="${pageContext.request.contextPath}/resources/images/bt_personal.png" class="textmiddle"/></a></span> 
			</div>
		</div>
		<div class="patno-phone">
			<img src="${pageContext.request.contextPath}/resources/images/icon_call.png" class="textmiddle"/><span class="text-indent">문의 : 서울센터 02)2001-1140 / 수원센터 031)303-0300</span>
			
		</div>
	</form>
</div>

<div id="agree-desc">
	<div class="agree-title">개인정보 수집 및 이용안내</div>
	
			<div class="agree-contents">1.귀하의 소중한 개인정보(주민번호)는 의료법인 강북삼성병원 종합건진센터의 건강검진을 위한 본인확인 절차로만 사용하게 됩니다.</div>
			<div class="agree-contents">2. 건강검진을 위하여 수집된 개인정보의 보유 및 이용기간은 건강검진 당해 년도에 해당하는 기간으로 합니다.</div>
	
	<div class="close-btn">
		<a href="javascript:closeAgree();">닫기</a>
	</div>
</div>

</body>

</html>
