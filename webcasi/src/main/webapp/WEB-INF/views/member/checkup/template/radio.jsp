<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="question radio" >
	<c:if test="${!empty question.description}">
		<div class="question-short-desc">
			${question.help.contents}
		</div>
	</c:if>
	<c:set var="cssPostfix" scope="request">
		<c:choose>
			<c:when test="${questionIndex eq 0 || questionIndex % 2 eq 0}">
				<c:out value="even"/>
			</c:when>
			<c:otherwise>
				<c:out value="odd"/>
			</c:otherwise>
		</c:choose>
	</c:set>
	<div class="question-title-top ${cssPostfix}">
		<div class="question-title-left"></div>
		<div class="question-title-center"></div>
		<div class="question-title-right"></div>
	</div>
	<div class="question-title ${cssPostfix}" id="question-${question.id}">
		<c:choose>
			<c:when test="${empty questionNo}">
				${question.questionNo}.  ${question.title}
			</c:when>
			<c:otherwise>
				${questionNo}.  ${question.title}
			</c:otherwise>
		</c:choose>	
	</div>

	<div class="question-item-container">
		<c:set var="resultItems" value="${resultMap[question.id]}"/>
		<c:forEach var="item" items="${question.childItems}" varStatus="itemStatus">
		<c:if test="${item.active eq true}">
			<c:set var="radioRowClass" value="radio-row"/>
			<c:if test="${item.type eq 'RADIO_HOR'}">
				<c:set var="radioRowClass" value="radio-float-row"/>
			</c:if>
			<c:if test="${item.type eq 'RADIO_IMAGE'}">
				<c:set var="radioRowClass" value="radio-image-row"/>
			</c:if>
			<div class="question-item-row ${radioRowClass}">
				<c:set var="innerItem" value="${resultItems[item.id]}"/>
				<c:choose>
					<c:when test="${empty innerItem}">
						<c:if test="${item.type eq 'RADIO_IMAGE'}">
							<div class="radio-image">
								<img src="${pageContext.request.contextPath}/resources/images/thumbnail/${item.thumnailImage}" width="80"/>
							</div>
						</c:if>
						<c:set var="radioClass" value="radio-obj"/>
						<c:if test="${item.type eq 'RADIO_RADIO' || item.type eq 'RADIO_SUBJ_HOUR_MINUTE' || item.type eq 'RADIO_HOR' 
							|| item.type eq 'RADIO_SUBJ_1'}">
							<c:set var="radioClass" value="radio-short-obj"/>
						</c:if>
						<div class="${radioClass}">
							<input id="${item.id}" questionId="${question.id}" itemId="${item.id}" type="radio" name="${question.id}" 
								value="${item.id}" existChild="${item.existChildQuestion}" itemType="${item.type}" questionType="${question.type}" 
								questionSortOrder="${questionIndex}" itemSortOrder="${itemStatus.index}"
								itemTotalCount="${question.childItemCount}"><a href="javascript:clickRadioLink('${item.id}');">${item.title}</a>
						</div>
						<c:if test="${item.type eq 'RADIO_RADIO'}">
							<c:set var="embededItemResult" value="${innerItem.objectiveValue2}"/>
							<div class="short-float-text">(</div>
							<c:forEach var="embededItem" items="${item.childItems}">
								<c:choose>
									<c:when test="${embededItem.key eq embededItemResult}">
										<c:set var="checkedFlag" value="checked"/>
									</c:when>
									<c:otherwise>
										<c:set var="checkedFlag" value=""/>
									</c:otherwise>
								</c:choose>
								<div class="embeded-item">
									<input id="embeded_${item.id}_${embededItem.key}" embededItemId="${embededItem.key}" itemId="${item.id}" questionId="${question.id}" type="radio" name="${item.id}" 
											value="${embededItem.key}" itemType="${item.type}" existChild="${item.existChildQuestion}" questionType="${question.type}" ${checkedFlag} disabled>
									<a href="javascript:clickRadioLink('embeded_${item.id}_${embededItem.key}');">${embededItem.value}</a>
								</div>
							</c:forEach>
							<div class="short-float-text">)</div>
						</c:if>
						<c:if test="${item.type eq 'RADIO_SUBJ_HOUR_MINUTE'}">
							<div class="short-float-text">(</div>
							<div class="long-float-text">
								<c:if test="${!empty item.preText}">
									<span class="pretext">${item.preText}</span>
								</c:if>
								<c:set var="hourValue" scope="request">
									<c:out value="${innerItem.strValue}" default="00"/>
								</c:set>
								<span><input type=text questionId="${question.id}" itemId="${item.id}" name="${question.id}_str1" subj1="true" 
									size=5 itemType="${item.type}" validator="NUMBER" minRange="${item.minRange}" maxRange="${item.maxRange}" value="${hourValue}" disabled></span>
								<span class="innertext">시간</span>
								<span><input type=text questionId="${question.id}" itemId="${item.id}" name="${question.id}_str2" subj2="true" 
									size=5 itemType="${item.type}" validator="MINUTE" value="${innerItem.strValue2}" disabled></span>
								<span class="innertext">분</span>
								<c:if test="${!empty item.postText}">
									<span class="posttext">${item.postText}</span>
								</c:if>
							</div>
							<div class="short-float-text">)</div>
						</c:if>
						<c:if test="${item.type eq 'RADIO_SUBJ_1'}">
							<div class="short-float-text"></div>
							<div class="long-float-text">
								<c:if test="${!empty item.preText}">
									<span class="pretext">${item.preText}</span>
								</c:if>
								<span><input type=text questionId="${question.id}" itemId="${item.id}" name="${question.id}_str1" 
									size=10 itemType="${item.type}" validator="${item.validator}" value="${innerItem.strValue}" 
									minRange="${item.minRange}" maxRange="${item.maxRange}" existChild="${item.existChildQuestion}" disabled></span>
								
								<c:if test="${!empty item.postText}">
									<span class="posttext">${item.postText}</span>
								</c:if>
							</div>
							<div class="short-float-text"></div>
						</c:if>
						<div class="question-submitted-flag">
							<a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted" questionId="${question.id}" existChild="${item.existChildQuestion}"></a>
						</div>
					</c:when>
					<c:otherwise>
					<!-- 이미 답변을 선택한 경우임 -->
						<c:if test="${item.type eq 'RADIO_IMAGE'}">
							<div class="radio-image">
								<img src="${pageContext.request.contextPath}/resources/images/thumbnail/${item.thumnailImage}" width="80"/>
							</div>
						</c:if>				
						<c:set var="radioClass" value="radio-obj"/>
						<c:if test="${item.type eq 'RADIO_RADIO' || item.type eq 'RADIO_SUBJ_HOUR_MINUTE' || item.type eq 'RADIO_HOR'}">
							<c:set var="radioClass" value="radio-short-obj"/>
						</c:if>
						<div class="${radioClass}">
							<input id="${item.id}" questionId="${question.id}" itemId="${item.id}" type="radio" name="${question.id}" 
								value="${item.id}" existChild="${item.existChildQuestion}" itemType="${item.type}" 
								questionSortOrder="${questionIndex}" itemSortOrder="${itemStatus.index}" 
								itemTotalCount="${question.childItemCount}" questionType="${question.type}" checked><a href="javascript:clickRadioLink('${item.id}');">${item.title}</a>
						</div>
						<c:if test="${item.type eq 'RADIO_RADIO'}">
							<c:set var="embededItemResult" value="${innerItem.objectiveValue2}"/>
							<div class="short-float-text">(</div>
							<c:forEach var="embededItem" items="${item.childItems}">
								<c:choose>
									<c:when test="${embededItem.key eq embededItemResult}">
										<c:set var="checkedFlag" value="checked"/>
									</c:when>
									<c:otherwise>
										<c:set var="checkedFlag" value=""/>
									</c:otherwise>
								</c:choose>
								<div class="embeded-item">
									<input id="embeded_${item.id}_${embededItem.key}" embededItemId="${embededItem.key}" itemId="${item.id}" questionId="${question.id}" type="radio" name="${item.id}" 
											value="${embededItem.key}" itemType="${item.type}" existChild="${item.existChildQuestion}" questionType="${question.type}" ${checkedFlag}>
									<a href="javascript:clickRadioLink('embeded_${item.id}_${embededItem.key}');">${embededItem.value}</a>		
								</div>
							</c:forEach>
							<div class="short-float-text">)</div>
						</c:if>			
						<c:if test="${item.type eq 'RADIO_SUBJ_HOUR_MINUTE'}">
							<div class="short-float-text">(</div>
							<div class="long-float-text">
								<c:if test="${!empty item.preText}">
									<span class="pretext">${item.preText}</span>
								</c:if>
								<c:set var="hourValue" scope="request">
									<c:out value="${innerItem.strValue}" default="00"/>
								</c:set>
								<span><input type=text questionId="${question.id}" itemId="${item.id}" name="${question.id}_str1" 
									subj1="true" size=5 itemType="${item.type}" validator="HOUR" value="${hourValue}"></span>
								<span class="innertext">시간</span>
								<span><input type=text questionId="${question.id}" itemId="${item.id}" name="${question.id}_str2" subj2="true" size=5 itemType="${item.type}" validator="MINUTE" value="${innerItem.strValue2}"></span>
								<span class="innertext">분</span>
								<c:if test="${!empty question.postText}">
									<span class="posttext">${question.postText}</span>
								</c:if>
							</div>
							<div class="short-float-text">)</div>
						</c:if>		
						<c:if test="${item.type eq 'RADIO_SUBJ_1'}">
							<div class="short-float-text"></div>
							<div class="long-float-text">
								<c:if test="${!empty item.preText}">
									<span class="pretext">${item.preText}</span>
								</c:if>
								<span><input type=text questionId="${question.id}" itemId="${item.id}" name="${question.id}_str1" size=10 
									itemType="${item.type}" validator="${item.validator}" value="${innerItem.strValue}"  minRange="${item.minRange}" maxRange="${item.maxRange}"
									existChild="${item.existChildQuestion}" ></span>
								
								<c:if test="${!empty item.postText}">
									<span class="posttext">${item.postText}</span>
								</c:if>
							</div>
							<div class="short-float-text"></div>
						</c:if>
						<div class="question-submitted-flag">
							<a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted" questionId="${question.id}" existChild="${item.existChildQuestion}">
							<c:if test="${item.existChildQuestion}">
								<img src="${pageContext.request.contextPath}/resources/images/bt_reply.png"/>
							</c:if>	
							</a></div>
					</c:otherwise>
				</c:choose>
				</div>
			</c:if>
		</c:forEach>
		<!-- 질문에 답변을 했는지 여부를 체크해놓는다. -->
		<c:choose>
			<c:when test="${empty resultItems && question.required}">
				<input type=hidden id="complete_flag_${question.id}" questionId="${question.id}" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${question.id}" questionId="${question.id}" value="true"/>
			</c:otherwise>
		</c:choose>
	</div>	
	<div class="question-item-container-bottom">
		<div class="question-item-container-bottom-left"></div>
		<div class="question-item-container-bottom-center"></div>
		<div class="question-item-container-bottom-right"></div>
	</div>
	
</div>