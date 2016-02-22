<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>질문</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/jqGrid-4.4.1/css/ui.jqgrid.css"/>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/jquery.multiselect.css"/>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/jquery-te-Style.css"/>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.multiselect.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-te-1.0.6.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jqGrid-4.4.1/js/jquery.jqGrid.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jqGrid-4.4.1/js/i18n/grid.locale-kr.js"></script>
	<script>
		//var _requestPath = "${pageContext.request.contextPath}";
	</script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/admin/question.js"></script>
</head>
<body>
<input type="hidden" id="searchCategoryType" value="${searchCategoryType}" />
<input type="hidden" id="searchMasterId" value="${searchMasterId}" />
<input type="hidden" id="searchGroupId" value="${groupId}" />
<input type="hidden" id="searchQuestionId" value="${searchQuestionId}" />
<input type="hidden" id="searchItemId" value="${searchItemId}" />

<div id="group-container" class="ui-widget">
  <div>
  	<span>${title}</span><button id="create-question">추가</button><button id="list">위로</button>
  	<p>${itemTitle}</p>
  </div>
</div>
<br>
<table id="questionGrid"></table>
<br>
<div id="group-container" class="ui-widget">

	<div>
		<button id="copy-Question" style="float : left; width : 200px; height : 25px;">질문 및 답변 가져오기</button>
		<span style="float : left;">복사할 질문 ID</span>
		<input type="text" name="copyQuestionId" id="copyQuestionId" style="float : left;" class="text ui-widget-content ui-corner-all"/>
		<button id="copy-item" style="float : left; width : 120px; height : 25px;">답변만 가져오기</button>
  	</div>


  <div>
  	<span>&nbsp;</span>
	<button id="create-item">추가</button>
  </div>
  <br>
</div>
<table id="itemGrid"></table>



<div id="dialog-form" title="질문" class="hidden" >
	<form id="questionForm" action="${pageContext.request.contextPath}/admin/saveQuestion" method="POST" enctype="multipart/form-data">
		<input type="hidden" name="parentGroupId" id="parentGroupId" value="${groupId}" />
		<input type="hidden" name="parentQuestionId" id="parentQuestionId" value="${questionId}" />
		<input type="hidden" name="parentItemId" id="parentItemId" value="${itemId}" />
		<input type="hidden" name="depth" id="depth" value="${depth}" />
		
		<input type="hidden" name="questionId" id="questionId" />
		<input type="hidden" name="helpId" id="helpId" />
		<fieldset>
			<legend><span id="viewQuestion"></span></legend>
			<ol>
				<li <c:if test="${empty itemId}">class="hidden"</c:if>>
				    <label class="label">상위답변</label>
				    <select name="parentItems" id="parentItems" multiple="multiple" size="5" >
				    	<c:forEach var="itemRow" items="${items}">
						<option value="${itemRow.id}" <c:if test="${itemRow.id eq itemId}"> selected="selected" </c:if>>${itemRow.title}
					 	</c:forEach>
					</select>
				</li>
				<li>
				    <label class="label">항목번호</label>
				    <input type="text" name="questionNo" id="questionNo" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
					<label for="type" class="label">유형</label>
					<select name="type" id="type" class="text ui-widget-content ui-corner-all">
						<c:forEach var="questionTypeRow" items="${questionTypes}">
						<option value="${questionTypeRow}">${questionTypeRow.label}
					 	</c:forEach>
					</select>
				</li>
				<li>
					<label class="label">질문</label>
		    		<input type="text" name="title" id="title" class="text ui-widget-content ui-corner-all" size=75/>
				</li>
				<li>
				    <label class="label">Ask Code</label>
				    <input type="text" name="ocsAskCode" id="ocsAskCode" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				  	<label class="label">NoAnswer Code</label>
				    <input type="text" name="ocsNoAnswerCode" id="ocsNoAnswerCode" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label class="label">Ask2 Code</label>
				    <input type="text" name="ocsAskCode2" id="ocsAskCode2" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				  	<label class="label">NoAnswer2 Code</label>
				    <input type="text" name="ocsNoAnswerCode2" id="ocsNoAnswerCode2" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label class="label">Ask3 Code</label>
				    <input type="text" name="ocsAskCode3" id="ocsAskCode3" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				  	<label class="label">NoAnswer3 Code</label>
				    <input type="text" name="ocsNoAnswerCode3" id="ocsNoAnswerCode3" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li class="hidden">
					<label>설명</label>
				    <input type="text" name="description" id="description" class="text all ui-widget-content ui-corner-all" size=80/>
				</li>
				<li>
					<label class="label">성별</label>
				    <select name="gender" id="gender" class="text ui-widget-content ui-corner-all">
				    	<c:forEach var="genderRow" items="${genders}">
						<option value="${genderRow}">${genderRow}
					 	</c:forEach>
					</select>
				</li>
				<li>
					<label class="label">Validator</label>
		    		<select name="validator" id="validator" class="text ui-widget-content ui-corner-all">
						<c:forEach var="validatorTypeRow" items="${validatorTypes}">
						<option value="${validatorTypeRow}" <c:if test="${validatorTypeRow eq 'NONE'}"> selected="selected" </c:if>>${validatorTypeRow}
					 	</c:forEach>
					</select>
				</li>
				<li>
					<label class="label">최소값</label>
				    <input type="text" name="minRange" id="minRange" class="text ui-widget-content ui-corner-all"/>
				    (Validator가 Number일때만 의미있음)
				</li>
				<li>
					<label class="label">최대값</label>
				    <input type="text" name="maxRange" id="maxRange" class="text ui-widget-content ui-corner-all"/>
				    (Validator가 Number일때만 의미있음)
				</li>				
				<li>
					<label class="label">주관식</label>
				    <input type="text" name="preText" id="preText" class="text ui-widget-content ui-corner-all"/>()<input type="text" name="postText" id="postText" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
					<label>필수</label>
				    <input type="checkbox" name="required" id="required" value="1" class="ui-widget-content ui-corner-all" />
				    <label>스트레스 측정</label>
				    <input type="checkBox" name="navigationStressFlag" id="navigationStressFlag"value="1" class="text ui-widget-content ui-corner-all"/>
				    <label>영양설문 진행여부</label>
				    <input type="checkBox" name="navigationNutritionFlag" id="navigationNutritionFlag" value="1" class="text ui-widget-content ui-corner-all"/>
				</li>
				
				<li>
					<label>sortOrder</label>
		    		<input type="text" name="sortOrder" id="sortOrder" value ="999" class="ui-widget-content ui-corner-all" />
				</li>
			</ol>	
		</fieldset>
		<br>
	<c:if test="${searchCategoryType eq 'CHECK_LIST'}">
		<fieldset>
			<legend></legend>
			<ol>
				<li>
					<label>검진당일만 사용</label>
				    <input type="checkbox" name=checkListRequired id="checkListRequired" value="1" class="ui-widget-content ui-corner-all" />
				</li>
				<li>
					<label for="nurseQuestionType">간호사 체크 유형</label>
					<select name="nurseQuestionType" id="nurseQuestionType" class="text ui-widget-content ui-corner-all">
						<c:forEach var="questionTypeRow" items="${questionTypes}">
						<option value="${questionTypeRow}">${questionTypeRow.label}
					 	</c:forEach>
					</select>
				</li>
				<li>
				  	<label class="label">Answer Code</label>
				    <input type="text" name="nurseOcsAnswer" id="nurseOcsAnswer" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label class="label">Ask Code</label>
				    <input type="text" name="nurseOcsAskCode" id="nurseOcsAskCode" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				  	<label class="label">NoAnswer Code</label>
				    <input type="text" name="nurseOcsNoAnswerCode" id="nurseOcsNoAnswerCode" class="text ui-widget-content ui-corner-all"/>
				</li>
		    </ol>
			<button id="nurseItemAdd">간호사 항목 아이템 추가</button>
    		<ul id="nurse-item"></ul>
		</fieldset>
	</c:if>	
		<fieldset>
			<legend>Help</legend>
			<ol>
				<li>
				    <label class="label">Contents</label>
				    <div id="editor"></div>
				</li>
				<li>
					<label class="label">파일</label>
		    		<input type="file" name="attachFile" id="attachFile" class="text ui-widget-content ui-corner-all" />
		    		<div id=viewFile></div>
				</li>
		    </ol>
		</fieldset>
	</form>
</div>

<div id="dialog-form-item" title="답변" class="hidden">
	<form id="questionItemForm" action="${pageContext.request.contextPath}/admin/saveQuestionItem" method="POST" >
		<input type="hidden" name="parentQuestionId" id="parentQuestionId-item" />
		<input type="hidden" name="itemId" id="itemId-item" />
		<fieldset>
			<legend><span id="viewItem"></span></legend>
			<ol>
				<li>
					<label for="type">유형</label>
					<select name="type" id="type-item" class="text ui-widget-content ui-corner-all">
						<c:forEach var="questionTypeRow" items="${questionTypes}">
						<option value="${questionTypeRow}" <c:if test="${questionTypeRow eq 'CHECK'}"> selected="selected" </c:if>>${questionTypeRow.label}
					 	</c:forEach>
					</select>
				</li>
				<li>
					<label>OCS Answer</label>
				    <input type="text" name="ocsAnswer" id="ocsAnswer-item" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
					<label>답변</label>
		    		<input type="text" name="title" id="title-item" class="text ui-widget-content ui-corner-all" size=60/>
				</li>
				<li>
				    <label>Ask Code</label>
				    <input type="text" name="ocsAskCode" id="ocsAskCode-item" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label>NoAnswer Code</label>
				    <input type="text" name="ocsNoAnswerCode" id="ocsNoAnswerCode-item" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label>Ask Code2</label>
				    <input type="text" name="ocsAskCode2" id="ocsAskCode2-item" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label>NoAnswer2 Code</label>
				    <input type="text" name="ocsNoAnswerCode2" id="ocsNoAnswerCode2-item" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label>Ask Code3</label>
				    <input type="text" name="ocsAskCode3" id="ocsAskCode3-item" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label>NoAnswer3 Code</label>
				    <input type="text" name="ocsNoAnswerCode3" id="ocsNoAnswerCode3-item" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label>주관식1</label>
				    <input type="text" name="preText" id="preText-item" class="text ui-widget-content ui-corner-all"/>()<input type="text" name="postText" id="postText-item" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
				    <label>주관식2</label>
				    <input type="text" name="preText2" id="preText2-item" class="text ui-widget-content ui-corner-all"/>()<input type="text" name="postText2" id="postText2-item" class="text ui-widget-content ui-corner-all"/>
				</li>
				<li>
					<label>Validator</label>
		    		<select name="validator" id="validator-item" class="text ui-widget-content ui-corner-all">
						<c:forEach var="validatorTypeRow" items="${validatorTypes}">
						<option value="${validatorTypeRow}" <c:if test="${validatorTypeRow eq 'NONE'}"> selected="selected" </c:if>>${validatorTypeRow}
					 	</c:forEach>
					</select>
				</li>
				<li>
					<label class="label">최소값</label>
				    <input type="text" name="minRange" id="minRange-item" class="text ui-widget-content ui-corner-all"/>
				    (Validator가 Number일때만 의미있음)
				</li>
				<li>
					<label class="label">최대값</label>
				    <input type="text" name="maxRange" id="maxRange-item" class="text ui-widget-content ui-corner-all"/>
				    (Validator가 Number일때만 의미있음)
				</li>					
				<li>
					<label>이미지</label>
				    <input type="text" name="thumnailImage" id="thumnailImage-item" class="text ui-widget-content ui-corner-all" size=50/>
				</li>
				<li>
					<label>아이템그룹</label>
				    <select name="itemGroup" id="itemGroup-item" class="text ui-widget-content ui-corner-all">
						<option value="">
						<c:forEach var="nutritionItemTypeRow" items="${nutritionItemTypes}">
						<option value="${nutritionItemTypeRow}">${nutritionItemTypeRow.label}
					 	</c:forEach>
					</select>
				</li>
				<li>
					<label>없음</label>
				    <input type="checkbox" name="noneFlag" id="noneFlag-item" value="1" class="ui-widget-content ui-corner-all" />
				</li>
				
				<li>
					<label>sortOrder</label>
		    		<input type="text" name="sortOrder" id="sortOrder-item" value ="999" class="ui-widget-content ui-corner-all" />
				</li>
				
		    </ol>
		</fieldset>
		<fieldset>
			<legend>객관식 항목</legend>
			<button id="itemObjAdd-item">추가</button>
			<!-- ul>
				<span>키</span><span style="margin:80px">값</span><span style="margin:10px">ocsAskCode</span><span>ocsAnswer</span>
			</ul -->
    		<ul id="content-item"></ul>
		</fieldset>
	</form>
</div>
</body>
</html>