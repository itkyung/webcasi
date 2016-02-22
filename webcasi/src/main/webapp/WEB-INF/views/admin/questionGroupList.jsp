<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>질문 그룹</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/jqGrid-4.4.1/css/ui.jqgrid.css"/>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/jquery-te-Style.css"/>
	<script>
		//var masterStatus = "${master.status}";
		var masterStatus = "";
	</script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-te-1.0.6.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jqGrid-4.4.1/js/jquery.jqGrid.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jqGrid-4.4.1/js/i18n/grid.locale-kr.js"></script>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/admin/group.js"></script>
</head>
<body>
<div id="group-container" class="ui-widget">
  <div>
  	<span>${master.title} > 
	<select name="categoryType" id="categoryType">
		<c:forEach var="categoryTypeRow" items="${categoryTypes}">
		<option value="${categoryTypeRow}" <c:if test="${categoryType eq categoryTypeRow}">selected</c:if> >${categoryTypeRow.label}
	 	</c:forEach>
	</select>
	</span>
	<!-- button id="create-questionGroup" <c:if test="${master.status eq 'ACTIVE'}">class="hidden"</c:if> >추가</button-->
	<button id="create-questionGroup" >추가</button>
  </div>
</div>
<div id="dialog-form" title="질문 그룹" class="hidden">
	<form id="questionGroupForm" action="${pageContext.request.contextPath}/admin/saveQuestionGroup" method="POST" enctype="multipart/form-data">
		<input type="hidden" name="categoryTp" id="categoryTp" />
		<input type="hidden" name="masterId" id="masterId" value="${master.id}" />
		<input type="hidden" name="groupId" id="groupId" />
		<input type="hidden" name="attachFilePath" id="attachFilePath" />
		<input type="hidden" name="helpId" id="helpId" />
		<fieldset>
			<legend><span id="viewCategory"></span></legend>
			<ul>
				<li>
				    <label>항목번호</label>
				    <input type="text" name="groupNo" id="groupNo" class="text ui-widget-content ui-corner-all" size="5"/>
				</li>
				<li>
					<label for="name">유 형</label>
					<select name="groupType" id="groupType">
						<c:forEach var="groupTypeRow" items="${questionGroupTypes}">
						<option value="${groupTypeRow}">${groupTypeRow.label}
					 	</c:forEach>
					</select>
				</li>
				<li>
					<label>그 룹 명</label>
		    		<input type="text" name="title" id="title" class="text ui-widget-content ui-corner-all" />
				</li>
				<li class="hidden">
					<label>설명</label>
				    <input type="text" name="description" id="description" class="text all ui-widget-content ui-corner-all" size=50/>
				</li>
				<li>
					<label>성 별</label>
				    <select name="gender" id="gender">
				    	<c:forEach var="genderRow" items="${genders}">
						<option value="${genderRow}">${genderRow}
					 	</c:forEach>
					</select>
				</li>
				<li>
					<label>썸네일 이미지</label>
		    		<input type="text" name="thumbnailImage" id="thumbnailImage" class="text ui-widget-content ui-corner-all" />
				</li>
				<li>
					<label>간호사 수정 여부</label>
		    		<input type="checkbox" name="nurseEditable" id="nurseEditable" value="1" class="ui-widget-content ui-corner-all" />
				</li>
				
				<li>
					<label>sortOrder</label>
		    		<input type="text" name="sortOrder" id="sortOrder" value ="999" class="ui-widget-content ui-corner-all" />
				</li>
				
		    </ul>
		</fieldset>
		<fieldset>
			<legend>Help</legend>
			<ol>
				<li>
				    <label>Contents</label>
				    <div id="editor"></div>
				</li>
				<li>
					<label>파 일</label>
		    		<input type="file" name="attachFile" id="attachFile" class="text ui-widget-content ui-corner-all" />
		    		<div id=viewFile></div>
				</li>
		    </ol>
		</fieldset>
	</form>
</div>
<br>
<table id="groupGrid"></table>
</body>
</html>