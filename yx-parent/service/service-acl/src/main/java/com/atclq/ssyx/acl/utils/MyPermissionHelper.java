package com.atclq.ssyx.acl.utils;

import com.atclq.ssyx.model.acl.Permission;

import java.util.ArrayList;
import java.util.List;

public class MyPermissionHelper {

    public static List<Permission> buildPermission(List<Permission> allList){
        //最终封装数据的list集合
        List<Permission> resultList = new ArrayList<>();
        //遍历所有菜单的list集合（传过来的参数），找到菜单pid为0的菜单第一级菜单，调用findChildren方法从第一层开始往下找
        for (Permission permission : allList) {
            if (permission.getPid() == 0) {
                permission.setLevel(1);//找到pid为0的权限对象(即一级菜单),将其封装成一个新的权限对象
                resultList.add(findChildren(allList, permission));
            }
        }

        return resultList;
    }

    //递归查找子菜单
    //参数：所有菜单的list集合，当前节点父菜单的Permission对象
    private static Permission findChildren(List<Permission> allList,
                                           Permission parentPermission){
        //遍历所有菜单的list集合（传过来的参数），找到菜单pid与父菜单的id相同的菜单，进行封装之后，如果有子菜单，继续递归查找子菜单；直到没有子菜单，返回空list
        parentPermission.setChildren(new ArrayList<Permission>());
        for(Permission permission : allList){
            if(permission.getPid().longValue() == parentPermission.getId().longValue()){
                int level = parentPermission.getLevel() + 1;
                permission.setLevel(level);//找到pid与父权限对象id相同的权限对象(即子菜单),将其封装成一个新的权限对象

                if(parentPermission.getChildren() == null){//判断父权限对象是否有子权限对象（children属性是否为null），如果没有，则创建一个[空]的子权限对象  由于第一层的权限对象已经在本方法的第一行赋了一个[ ]值，所以第一层的权限对象的不为null
                    parentPermission.setChildren(new ArrayList<>());
                }

                parentPermission.getChildren().add(findChildren(allList, permission));
            }
        }
        return parentPermission;
    }

}
