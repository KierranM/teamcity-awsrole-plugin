
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
        <label for="${params.getRoleArn()}"> Role ARN: <l:star/></label>
    </th>
    <td>
        <props:textProperty name="<%=params.getRoleArn()%>" className="longField"/>
        <span class="error" id="error_${params.getRoleArn()}"></span>
    </td>
</tr>
<tr>
    <th>
        <label for="${params.getExternalId()}">External ID:</label>
    </th>
    <td>
        <props:textProperty name="<%=params.getExternalId()%>" className="longField"/>
        <span class="smallNote">Leave empty to use the build type ID</span>
    </td>
</tr>
<tr>
    <th>
        <label for="${params.getSessionName()}">Session Name:</label>
    </th>
    <td>
        <props:textProperty name="<%=params.getSessionName()%>" className="longField"/>
        <span class="smallNote">Leave empty to use the build type ID and build number</span>
    </td>
</tr>
<tr>
    <th>
        <label for="${params.getSessionTags()}">Session Tags:</label>
    </th>
    <td>
        <props:multilineProperty cols="40" rows="6" linkTitle="Session Tags" name="<%=params.getSessionTags()%>" className="longField"/>
        <span class="smallNote">Newline seperated list of tags in the format key=value</span>
    </td>
</tr>