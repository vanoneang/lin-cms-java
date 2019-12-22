package com.lin.cms.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lin.cms.demo.mapper.*;
import com.lin.cms.demo.model.*;
import com.lin.cms.demo.vo.PageResultVO;
import com.lin.cms.demo.bo.GroupPermissionsBO;
import com.lin.cms.demo.dto.admin.*;
import com.lin.cms.demo.dto.user.RegisterDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback
@Slf4j
@ActiveProfiles("test")
public class AdminServiceImplTest {

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private GroupPermissionMapper groupPermissionMapper;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserIdentityServiceImpl userIdentityService;


    public UserDO mockData() {
        UserDO user = UserDO.builder().nickname("pedro大大").username("pedro大大").build();
        GroupDO group1 = GroupDO.builder().name("测试分组12").info("just for test").build();
        GroupDO group2 = GroupDO.builder().name("测试分组11").info("just for test").build();
        userMapper.insert(user);
        groupMapper.insert(group1);
        groupMapper.insert(group2);
        List<UserGroupDO> relations = new ArrayList<>();
        UserGroupDO relation1 = new UserGroupDO(user.getId(), group1.getId());
        UserGroupDO relation2 = new UserGroupDO(user.getId(), group2.getId());
        relations.add(relation1);
        relations.add(relation2);
        userGroupMapper.insertBatch(relations);
        return user;
    }

    public GroupDO mockData1() {
        UserDO user1 = UserDO.builder().nickname("pedro大大").username("pedro大大").build();
        UserDO user2 = UserDO.builder().nickname("pedro小小").username("pedro小小").build();
        GroupDO group = GroupDO.builder().name("测试分组11").info("just for test").build();
        userMapper.insert(user1);
        userMapper.insert(user2);
        groupMapper.insert(group);
        List<UserGroupDO> relations = new ArrayList<>();
        UserGroupDO relation1 = new UserGroupDO(user1.getId(), group.getId());
        UserGroupDO relation2 = new UserGroupDO(user2.getId(), group.getId());
        relations.add(relation1);
        relations.add(relation2);
        userGroupMapper.insertBatch(relations);
        return group;
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getUsers() {
        GroupDO group = mockData1();
        PageResultVO<UserDO> page = adminService.getUserPageByGroupId(group.getId(), 10L, 0L);
        assertTrue(page.getTotal() > 0);
        boolean anyMatch = page.getItems().stream().anyMatch(it -> it.getUsername().equals("pedro大大"));
        assertTrue(anyMatch);
    }

    @Test
    public void changeUserPassword() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("pedro&佩德罗");
        dto.setPassword("123456");
        dto.setConfirmPassword("123456");
        UserDO user = userService.createUser(dto);
        assertEquals("pedro&佩德罗", user.getUsername());
        boolean valid = userIdentityService.verifyUsernamePassword(user.getId(), "pedro&佩德罗", "123456");
        assertTrue(valid);

        ResetPasswordDTO dto1 = new ResetPasswordDTO();
        dto1.setNewPassword("147258");
        dto1.setConfirmPassword("147258");
        boolean b = adminService.changeUserPassword(user.getId(), dto1);
        assertTrue(b);

        valid = userIdentityService.verifyUsernamePassword(user.getId(), "pedro&佩德罗", "147258");
        assertTrue(valid);
    }

    @Test
    public void deleteUser() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("pedro&佩德罗");
        dto.setPassword("123456");
        dto.setConfirmPassword("123456");
        UserDO user = userService.createUser(dto);
        assertEquals("pedro&佩德罗", user.getUsername());

        boolean b = adminService.deleteUser(user.getId());
        assertTrue(b);

        UserDO selected = userMapper.selectById(user.getId());
        assertNull(selected);
    }

    @Test
    public void updateUserInfo() {
    }

    @Test
    public void getGroupPage() {
        GroupDO group1 = GroupDO.builder().name("测试分组12").info("just for test").build();
        GroupDO group2 = GroupDO.builder().name("测试分组11").info("just for test").build();
        groupMapper.insert(group1);
        groupMapper.insert(group2);
        PageResultVO<GroupDO> page = adminService.getGroupPage(0L, 10L);
        assertTrue(page.getTotal() > 0);
        boolean anyMatch = page.getItems().stream().anyMatch(it -> it.getName().equals("测试分组12"));
        assertTrue(anyMatch);
    }

    @Test
    public void getGroup() {
        GroupDO group = GroupDO.builder().name("测试分组1").info("just for test").build();
        PermissionDO permission1 = PermissionDO.builder().name("权限1").module("炉石传说").build();
        PermissionDO permission2 = PermissionDO.builder().name("权限2").module("炉石传说").build();
        groupMapper.insert(group);
        permissionMapper.insert(permission1);
        permissionMapper.insert(permission2);
        List<GroupPermissionDO> relations = new ArrayList<>();
        GroupPermissionDO relation1 = new GroupPermissionDO(group.getId(), permission1.getId());
        GroupPermissionDO relation2 = new GroupPermissionDO(group.getId(), permission2.getId());
        relations.add(relation1);
        relations.add(relation2);
        groupPermissionMapper.insertBatch(relations);

        GroupPermissionsBO group1 = adminService.getGroup(group.getId());
        assertEquals("测试分组1", group1.getName());
        assertNotNull(group1.getId());
        boolean anyMatch = group1.getPermissions().stream().anyMatch(it -> {
            PermissionDO p = (PermissionDO) it;
            return p.getName().equals("权限1");
        });
        assertTrue(anyMatch);
    }

    @Test
    public void createGroup() {
        PermissionDO permission1 = PermissionDO.builder().name("权限1").module("炉石传说").build();
        PermissionDO permission2 = PermissionDO.builder().name("权限2").module("炉石传说").build();
        permissionMapper.insert(permission1);
        permissionMapper.insert(permission2);

        NewGroupDTO dto = new NewGroupDTO();
        dto.setName("测试分组1");
        dto.setInfo("just for test");
        dto.setPermissionIds(Arrays.asList(permission1.getId(), permission2.getId()));
        boolean ok = adminService.createGroup(dto);
        assertTrue(ok);

        QueryWrapper<GroupDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(GroupDO::getName, "测试分组1");
        GroupDO group = groupMapper.selectOne(wrapper);
        assertEquals("测试分组1", group.getName());
        assertEquals("just for test", group.getInfo());

        GroupPermissionsBO groupPermissions = adminService.getGroup(group.getId());
        boolean anyMatch = groupPermissions.getPermissions().stream().anyMatch(it -> {
            PermissionDO p = (PermissionDO) it;
            return p.getName().equals("权限1");
        });
        assertTrue(anyMatch);
    }

    @Test
    public void updateGroup() {
        GroupDO group = GroupDO.builder().name("测试分组1").info("just for test").build();
        groupMapper.insert(group);

        UpdateGroupDTO dto = new UpdateGroupDTO();
        dto.setName("测试分组1儿子");
        dto.setInfo("测试分组1儿子info");
        boolean ok = adminService.updateGroup(group.getId(), dto);
        assertTrue(ok);
        GroupDO selected = groupMapper.selectById(group.getId());
        assertEquals(selected.getName(), "测试分组1儿子");
        assertEquals(selected.getInfo(), "测试分组1儿子info");
    }

    @Test
    public void deleteGroup() {
        GroupDO group = GroupDO.builder().name("测试分组1").info("just for test").build();
        groupMapper.insert(group);

        boolean ok = adminService.deleteGroup(group.getId());
        assertTrue(ok);
        GroupDO selected = groupMapper.selectById(group.getId());
        assertNull(selected);
    }

    @Test
    public void dispatchPermission() {
        GroupDO group = GroupDO.builder().name("测试分组1").info("just for test").build();
        PermissionDO permission1 = PermissionDO.builder().name("权限1").module("炉石传说").build();
        PermissionDO permission2 = PermissionDO.builder().name("权限2").module("炉石传说").build();
        groupMapper.insert(group);
        permissionMapper.insert(permission1);
        permissionMapper.insert(permission2);
        List<GroupPermissionDO> relations = new ArrayList<>();
        GroupPermissionDO relation1 = new GroupPermissionDO(group.getId(), permission1.getId());
        GroupPermissionDO relation2 = new GroupPermissionDO(group.getId(), permission2.getId());
        relations.add(relation1);
        relations.add(relation2);
        groupPermissionMapper.insertBatch(relations);

        PermissionDO permission3 = PermissionDO.builder().name("权限3").module("炉石传说").build();
        permissionMapper.insert(permission3);
        DispatchPermissionDTO dto = new DispatchPermissionDTO();
        dto.setGroupId(group.getId());
        dto.setPermissionId(permission3.getId());
        boolean ok = adminService.dispatchPermission(dto);
        assertTrue(ok);
        GroupPermissionsBO groupPermissions = adminService.getGroup(group.getId());
        boolean anyMatch = groupPermissions.getPermissions().stream().anyMatch(it -> {
            PermissionDO p = (PermissionDO) it;
            return p.getName().equals("权限3") && p.getModule().equals("炉石传说");
        });
        assertTrue(anyMatch);
    }

    @Test
    public void dispatchPermissions() {
        GroupDO group = GroupDO.builder().name("测试分组1").info("just for test").build();
        PermissionDO permission1 = PermissionDO.builder().name("权限1").module("炉石传说").build();
        PermissionDO permission2 = PermissionDO.builder().name("权限2").module("炉石传说").build();
        groupMapper.insert(group);
        permissionMapper.insert(permission1);
        permissionMapper.insert(permission2);
        List<GroupPermissionDO> relations = new ArrayList<>();
        GroupPermissionDO relation1 = new GroupPermissionDO(group.getId(), permission1.getId());
        GroupPermissionDO relation2 = new GroupPermissionDO(group.getId(), permission2.getId());
        relations.add(relation1);
        relations.add(relation2);
        groupPermissionMapper.insertBatch(relations);

        PermissionDO permission3 = PermissionDO.builder().name("权限3").module("炉石传说").build();
        permissionMapper.insert(permission3);
        PermissionDO permission4 = PermissionDO.builder().name("权限4").module("炉石传说").build();
        permissionMapper.insert(permission4);

        DispatchPermissionsDTO dto = new DispatchPermissionsDTO();
        dto.setGroupId(group.getId());
        dto.setPermissionIds(Arrays.asList(permission3.getId(), permission4.getId()));
        boolean ok = adminService.dispatchPermissions(dto);
        assertTrue(ok);
        GroupPermissionsBO groupPermissions = adminService.getGroup(group.getId());
        boolean anyMatch = groupPermissions.getPermissions().stream().anyMatch(it -> {
            PermissionDO p = (PermissionDO) it;
            return p.getName().equals("权限3") && p.getModule().equals("炉石传说");
        });
        assertTrue(anyMatch);

        anyMatch = groupPermissions.getPermissions().stream().anyMatch(it -> {
            PermissionDO p = (PermissionDO) it;
            return p.getName().equals("权限4") && p.getModule().equals("炉石传说");
        });
        assertTrue(anyMatch);
    }

    @Test
    public void removePermissions() {
        GroupDO group = GroupDO.builder().name("测试分组1").info("just for test").build();
        PermissionDO permission1 = PermissionDO.builder().name("权限1").module("炉石传说").build();
        PermissionDO permission2 = PermissionDO.builder().name("权限2").module("炉石传说").build();
        PermissionDO permission3 = PermissionDO.builder().name("权限3").module("炉石传说").build();
        groupMapper.insert(group);
        permissionMapper.insert(permission1);
        permissionMapper.insert(permission2);
        permissionMapper.insert(permission3);
        List<GroupPermissionDO> relations = new ArrayList<>();
        GroupPermissionDO relation1 = new GroupPermissionDO(group.getId(), permission1.getId());
        GroupPermissionDO relation2 = new GroupPermissionDO(group.getId(), permission2.getId());
        GroupPermissionDO relation3 = new GroupPermissionDO(group.getId(), permission3.getId());
        relations.add(relation1);
        relations.add(relation2);
        relations.add(relation3);
        groupPermissionMapper.insertBatch(relations);

        RemovePermissionsDTO dto = new RemovePermissionsDTO();
        dto.setGroupId(group.getId());
        // 2, 3
        dto.setPermissionIds(Arrays.asList(permission3.getId(), permission2.getId()));
        boolean ok = adminService.removePermissions(dto);
        assertTrue(ok);
        GroupPermissionsBO groupPermissions = adminService.getGroup(group.getId());
        boolean anyMatch = groupPermissions.getPermissions().stream().anyMatch(it -> {
            PermissionDO p = (PermissionDO) it;
            return p.getName().equals("权限3") && p.getModule().equals("炉石传说");
        });
        assertFalse(anyMatch);

        anyMatch = groupPermissions.getPermissions().stream().anyMatch(it -> {
            PermissionDO p = (PermissionDO) it;
            return p.getName().equals("权限1") && p.getModule().equals("炉石传说");
        });
        assertTrue(anyMatch);
    }

    @Test
    public void getAllGroups() {
        GroupDO group = GroupDO.builder().name("测试分组1").info("just for test").build();
        groupMapper.insert(group);
        List<GroupDO> groups = adminService.getAllGroups();
        assertTrue(groups.size() > 0);
        boolean anyMatch = groups.stream().anyMatch(it -> it.getName().equals("测试分组1"));
        assertTrue(anyMatch);
    }
}