# finereport

## 思路

- 系统总管理员(root)在权限管理中开启分级授权选项，将授权权限赋给下级管理员的角色，同时
  配置该角色所能分配权限的角色(对象)，此时，下级管理员的角色登录系统时，就能将其
  有权授权的权限分配给对应的角色。
- 查看权限和授权权限
- 次级管理员必须拥有权限管理权限后，才能拥有授权权限
- 