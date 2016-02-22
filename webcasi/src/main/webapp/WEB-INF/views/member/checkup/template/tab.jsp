<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>

<script>
	$(document).ready(function(){
		$( "#tabs" ).tabs();
	});

</script>

<div class="question q-tab">
	<c:if test="${!empty question.description}">
		<div class="question-short-desc">
			${question.help.contents}
		</div>
	</c:if>
	<div class="question-title" id="question-${question.id}">
		${question.questionNo}. ${question.title}
	</div>
	
	<div class="question-item-container" >
		<div id="tabs">
			<ul class="tabs">
				
				<c:forEach var="childQuestion" items="${question.childQuestions}" varStatus="status">
					<c:if test="${childQuestion.active eq true}">
			    		<li id="tab-item-${childQuestion.id}"><a href="#tab${status.count}">${webcasi:substring(childQuestion.title,5)}</a></li>
			    	</c:if>
				</c:forEach>
			</ul>
			<c:forEach var="childQuestion" items="${question.childQuestions}"  varStatus="status">
				<c:if test="${childQuestion.active eq true}">
					<c:set var="frequencyResult" value="${frequencyResultMap[childQuestion.id]}"/>
					<c:set var="quantityResult" value="${quantityResultMap[childQuestion.id]}"/>
					<c:set var="loopCount" value="${status.count}" scope="request"/>
					<c:set var="quantityCount" value="0" scope="request"/>
					<div id="tab${loopCount}" class="tab-content">
						<div class="nutrition-second-question">
							<div class="sub-tab-title"><span class="sub-title-span">${childQuestion.title}</span></div>
							<div class="sub-question-title">
								<img src="${pageContext.request.contextPath}/resources/images/depth_icon4.png"  class="text-middle"/>
								<span>평균 섭취빈도에 응답하여 주십시오.</span>
							</div>
							
							<c:forEach var="sitem" items="${childQuestion.childItems}">
								<c:if test="${sitem.active eq true}">
									<c:if test="${sitem.itemGroupStr eq 'QUANTITY'}">
										<c:set var="quantityCount" value="${quantityCount + 1}" scope="request"/>
									</c:if>
								</c:if>
							</c:forEach>
							<c:set var="loopCountAttr" value="-1"/>
							<c:if test="${quantityCount eq 0}">
								<c:set var="loopCountAttr" value="${loopCount}"/>
							</c:if>
							
							<c:forEach var="sitem" items="${childQuestion.childItems}">
								<c:if test="${sitem.active eq true}">
									<c:if test="${sitem.itemGroupStr eq 'FREQUENCY'}">
										<c:choose>
										<c:when test="${sitem.id eq frequencyResult}">
											<div class="nutrition-radio2">
												<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${childQuestion.id}" name="frequency_${loopCount}" 
													itemGroup="FREQUENCY" itemType="RADIO" loopCount="${loopCountAttr}" checked/>
													<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
											</div>
											
										</c:when>
										<c:otherwise>
											<div class="nutrition-radio2">
												<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${childQuestion.id}" name="frequency_${loopCount}" 
													itemGroup="FREQUENCY" itemType="RADIO" loopCount="${loopCountAttr}"/>
													<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
											</div>
										</c:otherwise>
										</c:choose>
									</c:if>
									
								</c:if>
							</c:forEach>
							<c:choose>
								<c:when test="${empty frequencyResult}">
									<input type=hidden id="complete_flag_${childQuestion.id}_FREQUENCY" questionId="${childQuestion.id}" value="false" tab="true"/>
								</c:when>
								<c:otherwise>
									<input type=hidden id="complete_flag_${childQuestion.id}_FREQUENCY" questionId="${childQuestion.id}" value="true" tab="true"/>
								</c:otherwise>
							</c:choose>
						</div>
						
						<c:if test="${quantityCount > 0}">
							<div class="nutrition-second-question">
								<div class="sub-question-title">
									<img src="${pageContext.request.contextPath}/resources/images/depth_icon4.png"  class="text-middle"/>
									<span>평균 1회 섭취량을 선택하여 주십시오.</span>
								</div>
								<c:forEach var="sitem" items="${childQuestion.childItems}" varStatus="status">
									<c:if test="${sitem.itemGroupStr eq 'QUANTITY' && sitem.active eq true}">
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
													<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${childQuestion.id}" name="quantity_${loopCount}" 
														itemGroup="QUANTITY" itemType="RADIO" loopCount="${loopCount}" checked/>
														<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
												</div>	
												
											</c:when>
											<c:otherwise>
												<div style="height:20px;text-align : center;">
													<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${childQuestion.id}" name="quantity_${loopCount}" 
														itemGroup="QUANTITY" itemType="RADIO" loopCount="${loopCount}" />
														<a href="javascript:clickRadioLink('${sitem.id}');">${sitem.title}</a>
												</div>	
											</c:otherwise>
											</c:choose>				
										</div>
									</c:if>
								</c:forEach>
								<c:choose>
									<c:when test="${empty quantityResult && status.count > 0} ">
										<input type=hidden id="complete_flag_${childQuestion.id}_QUANTITY" questionId="${childQuestion.id}" value="false" tab="true"/>
									</c:when>
									<c:otherwise>
										<input type=hidden id="complete_flag_${childQuestion.id}_QUANTITY" questionId="${childQuestion.id}" value="true" tab="true"/>
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>
					</div>
				</c:if>
			</c:forEach>
		</div>
	</div>
</div>