<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>간호사페이지</title>
<script>
	$(document).ready(function(){

		$("#acptDate").datepicker({
			showOn : 'both',
			dateFormat : 'yy-MM-dd',
			buttonImage:  _requestPath + "/resources/images/calendar.png",
			buttonImageOnly: true,
			changeYear : true,
			changeMonth : true,
			maxDate : '+0D',
			dayNamesMin : ['일','월','화','수','목','금','토'],
			monthNames : ['01','02','03','04','05','06','07','08','09','10','11','12'],
			monthNamesShort : ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월']
		});
	
	});
	
	var currentPage = ${currentPage};
	var _requestPath = "${pageContext.request.contextPath}";
	
	function doSearch(){
		var acptDate = $("#acptDate").val();
		if(acptDate.length == 0){
			var patno = $("#patno").val();
			if(patno.length == 0){
				var resno = $("#resno").val();
				if(resno.length == 0){
					var name = $("#name").val();
					if(name.length==0){
						alert("접수일이나 수진번호,이름,주민번호중에 적어도 하나의 항목을 입력해야합니다.");
						return;
					}
				}
			}
		}
		
		$("#page").val(currentPage);
		$("#searchForm").submit();
		
	}
	
	function viewInstance(instanceId,patno){
		var url = _requestPath + "/nurse/view/" + patno + "?instanceId=" + instanceId;
		window.open(url,""+instanceId,'');
	}
	
</script>
</head>
<body>
	<c:url value="/nurse/home" var="searchUrl"/>
	<div id="nurse-container">
		<div class="nurse-search-condition">
			<form id="searchForm" action="${searchUrl}" method="POST">
			<input type="hidden" id="page" name="page"/>
			<div class="condition-row">
				<div class="condition-label">접수일</div>
				<div class="condition-value"><input type="text" name="acptDate" id="acptDate" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${acptDate}"/>" size=10/></div>
			</div>
			<div  class="condition-row">
				<div class="condition-label">수진번호</div>
				<div class="condition-value"><input type="text" name="patno" id="patno" value="${patno}" size=10/></div>
				<div class="condition-label">주민번호('-' 빼고)</div>
				<div class="condition-value"><input type="text" name="resno" id="resno" value="${resno}" size=15/></div>
				<div class="condition-label">이름</div>
				<div class="condition-value"><input type="text" name="name" id="name" value="${name}" size=15/></div>
				<div class="condition-label"><input type="button" value="찾기" onClick="javascript:doSearch();"/></div>
			</div>
			
			</form>
		</div>
		<div class="nurse-search-result">
			<table class="nurse-table">
				<tr class="nurse-th">
					<td class="nurse-col-long">예약일</td>
					<td class="nurse-col-long">접수일</td>
					<td class="nurse-col-short">수진번호</td>
					<td class="nurse-col-long">이름</td>
					<td class="nurse-col-short">성별</td>
				</tr>
			<c:forEach var="instance" items="${results}">
				<tr>
					<td><a href="javascript:viewInstance('${instance.id}','${instance.patno}')"><fmt:formatDate pattern="yyyy-MM-dd" value="${instance.reserveDate}" /></a></td>
					<td><fmt:formatDate pattern="yyyy-MM-dd" value="${instance.acptDate}" /></td>
					<td>${instance.patno}</td>
					<td>${instance.owner.name}</td>
					<td>${instance.owner.gender}</td>
				</tr>
			</c:forEach>
			</table>
		</div>
		
	
	</div>
	
</body>
</html>