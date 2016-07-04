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

package com.docdoku.api;


import com.docdoku.api.client.ApiException;
import com.docdoku.api.models.*;
import com.docdoku.api.services.AccountsApi;
import com.docdoku.api.services.WorkspacesApi;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.UUID;

@RunWith(JUnit4.class)
public class WorkspacesApiTest {

    @Test
    public void createWorkspaceTest() throws ApiException {
        WorkspaceDTO workspace = new WorkspaceDTO();
        String workspaceId = UUID.randomUUID().toString().substring(0,6);
        workspace.setId(workspaceId);
        workspace.setDescription("Generated by tests");
        workspace.setFolderLocked(false);
        WorkspaceDTO createdWorkspace = new WorkspacesApi(TestConfig.BASIC_CLIENT).createWorkspace(workspace, TestConfig.LOGIN);
        Assert.assertEquals(workspace,createdWorkspace);
    }

    @Test
    public void getWorkspaceList() throws ApiException {
        WorkspaceDTO workspace = new WorkspaceDTO();
        String workspaceId = UUID.randomUUID().toString().substring(0,6);
        workspace.setId(workspaceId);
        workspace.setDescription("Generated by tests");
        workspace.setFolderLocked(false);
        WorkspacesApi workspacesApi = new WorkspacesApi(TestConfig.BASIC_CLIENT);
        WorkspaceDTO createdWorkspace = workspacesApi.createWorkspace(workspace, TestConfig.LOGIN);
        WorkspaceListDTO workspacesForConnectedUser = workspacesApi.getWorkspacesForConnectedUser();
        Assert.assertTrue(workspacesForConnectedUser.getAllWorkspaces().contains(createdWorkspace));
    }

    @Test
    public void updateWorkspace() throws ApiException {
        WorkspaceDTO workspace = new WorkspaceDTO();
        String workspaceId = UUID.randomUUID().toString().substring(0,6);
        workspace.setId(workspaceId);
        workspace.setDescription("Generated by tests");
        workspace.setFolderLocked(false);
        WorkspacesApi workspacesApi = new WorkspacesApi(TestConfig.BASIC_CLIENT);
        WorkspaceDTO createdWorkspace = workspacesApi.createWorkspace(workspace, TestConfig.LOGIN);
        String newDescription = "Updated by tests";
        createdWorkspace.setDescription(newDescription);
        WorkspaceDTO updatedWorkspace = workspacesApi.updateWorkspace(workspaceId, createdWorkspace);
        Assert.assertEquals(updatedWorkspace.getDescription(),newDescription);
        Assert.assertEquals(updatedWorkspace,createdWorkspace);
    }

    @Test
    public void addUserInWorkspace() throws ApiException {
        AccountDTO newAccount = createAccount();
        UserDTO userToAdd = new UserDTO();
        userToAdd.setLogin(newAccount.getLogin());
        WorkspacesApi workspacesApi = new WorkspacesApi(TestConfig.BASIC_CLIENT);
        workspacesApi.addUser(TestConfig.WORKSPACE, userToAdd, null);
        List<UserDTO> usersInWorkspace = workspacesApi.getUsersInWorkspace(TestConfig.WORKSPACE);
        Assert.assertEquals(usersInWorkspace.stream().filter(userDTO -> userDTO.getLogin().equals(userToAdd.getLogin())).count(), 1);
    }

    @Test
    public void addUserInGroup() throws ApiException {
        AccountDTO newAccount = createAccount();
        UserDTO userToAdd = new UserDTO();
        userToAdd.setLogin(newAccount.getLogin());
        UserGroupDTO group = createGroup();
        WorkspacesApi workspacesApi = new WorkspacesApi(TestConfig.BASIC_CLIENT);
        workspacesApi.addUser(TestConfig.WORKSPACE, userToAdd, group.getId());
        List<UserDTO> usersInGroup = workspacesApi.getUsersInGroup(TestConfig.WORKSPACE, group.getId());
        Assert.assertEquals(usersInGroup.stream().filter(userDTO -> userDTO.getLogin().equals(userToAdd.getLogin())).count(),1);
    }

    private UserGroupDTO createGroup() throws ApiException {
        String groupId = UUID.randomUUID().toString().substring(0,6);
        UserGroupDTO group = new UserGroupDTO();
        group.setWorkspaceId(TestConfig.WORKSPACE);
        group.setId(groupId);
        return new WorkspacesApi(TestConfig.BASIC_CLIENT).createGroup(TestConfig.WORKSPACE, group);
    }


    private AccountDTO createAccount() throws ApiException {
        String login = "USER-"+ UUID.randomUUID().toString().substring(0,6);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setLogin(login);
        accountDTO.setEmail("my@email.com");
        accountDTO.setNewPassword("password");
        accountDTO.setLanguage("en");
        accountDTO.setName("Mr " + login);
        accountDTO.setTimeZone("CET");
        return new AccountsApi(TestConfig.GUEST_CLIENT).createAccount(accountDTO);
    }

}
