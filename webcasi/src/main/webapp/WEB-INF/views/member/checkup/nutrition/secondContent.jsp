<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div id="tab${loopCount}" class="tab-content">
	<div class="sub-tab-title"><span class="sub-title-span">${nutritionItem.itemTitle}</span></div>
	<c:if test="${!empty nutritionItem.whetherItems}">
	<c:set var="whetherResult" value="${whetherResultMap[nutritionItem.questionId]}"/>
		<div class="nutrition-second-question">
			<div class="sub-question-title">
				<img src="${pageContext.request.contextPath}/resources/images/depth_icon4.png"  class="text-middle"/><span>주로 섭취하는 음식에 표시하여 주십시오.</span>
			</div>
			<c:forEach var="sitem" items="${nutritionItem.whetherItems}">
				<c:choose>
				<c:when test="${fn:contains(whetherResult,sitem.id)}">
					<div class="nutrition-radio">
						<input type="checkbox" id="${sitem.id}" itemId="${sitem.id}" questionId="${nutritionItem.questionId}" name="whether_${loopCount}" 
							itemGroup="WHETHER"  checked/>
							<a href="javascript:clickCheckboxLink('${sitem.id}');">${sitem.title}</a>
					</div>
					
				</c:when>
				<c:otherwise>
					<div class="nutrition-radio">
						<input type="checkbox" id="${sitem.id}" itemId="${sitem.id}" questionId="${nutritionItem.questionId}" name="whether_${loopCount}" 
							itemGroup="WHETHER" />
							<a href="javascript:clickCheckboxLink('${sitem.id}');">${sitem.title}</a>
					</div>
					
				</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
		<c:choose>
			<c:when test="${empty whetherResult}">
				<input type=hidden id="complete_flag_${nutritionItem.questionId}_WHETHER" questionId="${nutritionItem.questionId}" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${nutritionItem.questionId}_WHETHER" questionId="${nutritionItem.questionId}" value="true"/>
			</c:otherwise>
		</c:choose>
	</c:if>
	<c:if test="${!empty nutritionItem.monthItems}">
		<c:set var="monthResult" value="${monthResultMap[nutritionItem.questionId]}"/>
		<div class="nutrition-second-question">
			<div class="sub-question-title">
				<img src="${pageContext.request.contextPath}/resources/images/depth_icon4.png"  class="text-middle"/><span>지난 1년 중 몇 계절 동안 섭취하셨습니까?</span>
			</div>
			<c:forEach var="sitem" items="${nutritionItem.monthItems}">
				<c:choose>
				<c:when test="${sitem.id eq monthResult}">
					<div class="nutrition-radio">
						<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${nutritionItem.questionId}" name="month_${loopCount}" 
							itemGroup="MONTH" checked/>
							<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
					</div>
					
				</c:when>
				<c:otherwise>
					<div class="nutrition-radio">
						<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${nutritionItem.questionId}" name="month_${loopCount}" 
							itemGroup="MONTH" />
							<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
					</div>
					
				</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
		<c:choose>
			<c:when test="${empty monthResult}">
				<input type=hidden id="complete_flag_${nutritionItem.questionId}_MONTH" questionId="${nutritionItem.questionId}" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${nutritionItem.questionId}_MONTH" questionId="${nutritionItem.questionId}" value="true"/>
			</c:otherwise>
		</c:choose>
	</c:if>
	
	<c:if test="${!empty nutritionItem.frequencyItems}">
		<c:set var="frequencyResult" value="${frequencyResultMap[nutritionItem.questionId]}"/>
		<div class="nutrition-second-question">
			<div class="sub-question-title">
				<img src="${pageContext.request.contextPath}/resources/images/depth_icon4.png" class="text-middle"/><span>평균 섭취 빈도에 응답하여 주십시오.</span>
			</div>
			<c:forEach var="sitem" items="${nutritionItem.frequencyItems}" varStatus="status">
				<c:choose>
				<c:when test="${sitem.id eq frequencyResult}">
					<div class="nutrition-radio">
						<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${nutritionItem.questionId}" 
							name="frequency_${loopCount}" itemGroup="FREQUENCY" itemSortOrder="${status.index}" 
							value="${sitem.id}" 
							itemTotalCount="${nutritionItem.frequencyItemCount}" checked/>
							<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
					</div>
					
				</c:when>
				<c:otherwise>
					<div class="nutrition-radio">
						<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${nutritionItem.questionId}" 
							name="frequency_${loopCount}" itemGroup="FREQUENCY" itemSortOrder="${status.index}" 
							value="${sitem.id}" 
							itemTotalCount="${nutritionItem.frequencyItemCount}"/>
							<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
					</div>
					
				</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
		<c:choose>
			<c:when test="${empty frequencyResult}">
				<input type=hidden id="complete_flag_${nutritionItem.questionId}_FREQUENCY" questionId="${nutritionItem.questionId}" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${nutritionItem.questionId}_FREQUENCY" questionId="${nutritionItem.questionId}" value="true"/>
			</c:otherwise>
		</c:choose>
		
		
	</c:if>
	
	<c:if test="${!empty nutritionItem.quantityItems}">
		<c:set var="quantityResult" value="${quantityResultMap[nutritionItem.questionId]}"/>
		<div class="nutrition-second-question">
			<div class="sub-question-title">
				<img src="${pageContext.request.contextPath}/resources/images/depth_icon4.png"  class="text-middle"/><span>평균 1회 섭취량을 선택하여 주십시오.</span>
			</div>
			<c:if test="${!empty nutritionItem.questionDesc}">
				<div class="sub-question-title-desc">
					${nutritionItem.questionDesc}
				</div>
			</c:if>
			<c:forEach var="sitem" items="${nutritionItem.quantityItems}" varStatus="status">
				<div class="nutrition-radio-image">
					
					<c:if test="${!empty sitem.thumnailImage}">
						<div style="text-align : center;">
							<c:choose>
							<c:when test="${empty sitem.thumnailImage}">
								<img src="${pageContext.request.contextPath}/resources/images/thumbnail/nutrition_noimg.png" width="90" height="90"/>
							</c:when>
							<c:otherwise>
								<img src="${pageContext.request.contextPath}/resources/images/thumbnail/${sitem.thumnailImage}" width="90" height="90"/>
							</c:otherwise>
							</c:choose>
						</div>
					</c:if>
					
					<c:choose>
					<c:when test="${sitem.id eq quantityResult}">
						<div style="height:20px;text-align : center;">
							<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${nutritionItem.questionId}" 
								name="quantity_${loopCount}" itemGroup="QUANTITY" itemSortOrder="${status.index}" 
								value="${sitem.id}" 
								itemTotalCount="${nutritionItem.quantityItemCount}" loopCount="${loopCount}" checked/>
								<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
						</div>	
						
					</c:when>
					<c:otherwise>
						<div style="height:20px;text-align : center;">
							<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${nutritionItem.questionId}" 
								name="quantity_${loopCount}" itemGroup="QUANTITY" itemSortOrder="${status.index}" 
								value="${sitem.id}" loopCount="${loopCount}"  
								itemTotalCount="${nutritionItem.quantityItemCount}"/>
								<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
						</div>	
					
					</c:otherwise>
					</c:choose>				
				</div>
			</c:forEach>
		</div>
		<c:choose>
			<c:when test="${empty quantityResult}">
				<input type=hidden id="complete_flag_${nutritionItem.questionId}_QUANTITY" questionId="${nutritionItem.questionId}" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${nutritionItem.questionId}_QUANTITY" questionId="${nutritionItem.questionId}" value="true"/>
			</c:otherwise>
		</c:choose>
		
	</c:if>
	
	
</div>