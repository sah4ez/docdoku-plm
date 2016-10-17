/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2015 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.docdoku.server.rest;

import com.docdoku.core.common.User;
import com.docdoku.core.common.Workspace;
import com.docdoku.core.exceptions.*;
import com.docdoku.core.notification.TagUserSubscription;
import com.docdoku.core.security.UserGroupMapping;
import com.docdoku.core.services.INotificationManagerLocal;
import com.docdoku.core.services.IUserManagerLocal;
import com.docdoku.server.rest.dto.TagSubscriptionDTO;
import com.docdoku.server.rest.dto.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
@Api(hidden = true, value = "users", description = "Operations about users")
@DeclareRoles(UserGroupMapping.REGULAR_USER_ROLE_ID)
@RolesAllowed(UserGroupMapping.REGULAR_USER_ROLE_ID)
public class UserResource {


    @Inject
    private IUserManagerLocal userManager;

    @Inject
    private INotificationManagerLocal notificationManager;

    private Mapper mapper;

    private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());

    public UserResource() {
    }

    @PostConstruct
    public void init() {
        mapper = DozerBeanMapperSingletonWrapper.getInstance();
    }

    @GET
    @ApiOperation(value = "Get users", response = UserDTO.class, responseContainer = "List")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO[] getUsersInWorkspace(@PathParam("workspaceId") String workspaceId)
            throws EntityNotFoundException, AccessRightException, UserNotActiveException {

        User[] users = userManager.getUsers(workspaceId);
        UserDTO[] dtos = new UserDTO[users.length];

        for (int i = 0; i < users.length; i++) {
            dtos[i] = mapper.map(users[i], UserDTO.class);
        }

        return dtos;
    }

    @GET
    @ApiOperation(value = "Get current user details", response = UserDTO.class)
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO whoami(@PathParam("workspaceId") String workspaceId)
            throws EntityNotFoundException, UserNotActiveException {

        User user = userManager.whoAmI(workspaceId);
        return mapper.map(user, UserDTO.class);
    }

    @GET
    @ApiOperation(value = "Get admin for workspace", response = UserDTO.class)
    @Path("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getAdminInWorkspace(@PathParam("workspaceId") String workspaceId)
            throws EntityNotFoundException {

        Workspace workspace = userManager.getWorkspace(workspaceId);
        UserDTO userDTO = mapper.map(workspace.getAdmin(), UserDTO.class);
        userDTO.setWorkspaceId(workspaceId);
        return userDTO;
    }


    @GET
    @ApiOperation(value = "Get tag subscriptions of user", response = TagSubscriptionDTO.class, responseContainer = "List")
    @Path("{login}/tag-subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public TagSubscriptionDTO[] getTagSubscriptionsForUser(@PathParam("workspaceId") String workspaceId, @PathParam("login") String login) throws UserNotFoundException, AccessRightException, UserNotActiveException, WorkspaceNotFoundException, WorkspaceNotEnabledException {
        List<TagUserSubscription> subs = notificationManager.getTagUserSubscriptionsByUser(workspaceId, login);

        TagSubscriptionDTO[] subDTOs = new TagSubscriptionDTO[subs.size()];
        for (int i = 0; i < subs.size(); i++) {
            subDTOs[i] = mapper.map(subs.get(i), TagSubscriptionDTO.class);
        }
        return subDTOs;
    }

    @PUT
    @ApiOperation(value = "Update or create tag subscription of user", response = TagSubscriptionDTO.class)
    @Path("{login}/tag-subscriptions/{tagName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSubscription(@PathParam("workspaceId") String workspaceId,
                                       @PathParam("login") String login,
                                       @PathParam("tagName") String tagName,
                                       @ApiParam(required = true, value = "Tag subscription to update or create") TagSubscriptionDTO subDTO)
            throws EntityNotFoundException, AccessRightException, UserNotActiveException {


        notificationManager.createOrUpdateTagUserSubscription(workspaceId,
                login,
                tagName,
                subDTO.isOnIterationChange(),
                subDTO.isOnStateChange());
        subDTO.setTag(tagName);
        try {
            return Response.created(URI.create(URLEncoder.encode(tagName, "UTF-8"))).entity(subDTO).build();
        } catch (UnsupportedEncodingException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            return Response.ok().build();
        }
    }

    @DELETE
    @ApiOperation(value = "Delete tag subscription of user", response = Response.class)
    @Path("{login}/tag-subscriptions/{tagName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSubscription(@PathParam("workspaceId") String workspaceId,
                                       @PathParam("login") String login,
                                       @PathParam("tagName") String tagName) throws UserNotFoundException, AccessRightException, UserNotActiveException, WorkspaceNotFoundException, WorkspaceNotEnabledException {

        notificationManager.removeTagUserSubscription(workspaceId, login, tagName);
        return Response.ok().build();
    }
}
