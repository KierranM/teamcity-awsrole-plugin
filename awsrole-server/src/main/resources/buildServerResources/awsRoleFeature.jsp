
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="params" class="com.glassechidna.teamcity.awsrole.AwsRoleParametersProvider"/>

<tr>
    <td colspan="2">
        <em>Assume an AWS Role</em>
    </td>
</tr>
<tr>
    <th>
        <label for="${params.roleArn}"> Role ARN: <l:star/></label>
    </th>
    <td>
        <props:textProperty name="${params.roleArn}" className="longField"/>
        <span class="error" id="error_${params.roleArn}"></span>
    </td>
</tr>
<tr>
    <th>
        <label for="${params.externalId}">External ID:</label>
    </th>
    <td>
        <props:textProperty name="${params.externalId}" className="longField"/>
    </td>
</tr>
<tr>
    <th>
        <label for="${params.sessionName}">Session Name:</label>
    </th>
    <td>
        <props:textProperty name="${params.sessionName}" className="longField"/>
    </td>
</tr>
<tr>
    <th>
        <label for="${params.sessionDuration}">Session Duration:</label>
    </th>
    <td>
        <props:selectProperty name="${params.sessionDuration}" className="longField">
            <props:option value="900">15 Minutes</props:option>
            <props:option value="1600">30 Minutes</props:option>
            <props:option value="3600">1 Hour</props:option>
            <props:option value="7200">2 Hours</props:option>
            <props:option value="14400">4 Hours</props:option>
            <props:option value="28800">8 Hours</props:option>
            <props:option value="43200">12 Hours</props:option>
        </props:selectProperty>
        <span class="error" id="error_${params.sessionDuration}"></span>
    </td>
</tr>
<tr>
    <th>
        <label for="${params.sessionTags}">Session Tags:</label>
    </th>
    <td>
        <props:multilineProperty cols="40" rows="6" linkTitle="Session Tags" name="${params.sessionTags}" className="longField"/>
        <span class="smallNote">Newline seperated list of tags in the format key=value</span>
    </td>
</tr>