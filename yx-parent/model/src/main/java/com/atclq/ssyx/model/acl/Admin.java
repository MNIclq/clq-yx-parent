package com.atclq.ssyx.model.acl;

import com.atclq.ssyx.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder//可以通过@Builder注解来生成建造者模式的Builder类，Builder类可以用来构造对象，Builder类中包含了所有必要的属性，通过调用build()方法可以生成对象。
@ApiModel(description = "用户")
@TableName("admin")
public class Admin extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户名")
	@TableField("username")
	private String username;

	@ApiModelProperty(value = "密码")
	@TableField("password")
	private String password;

	@ApiModelProperty(value = "昵称")
	@TableField("name")
	private String name;

	@ApiModelProperty(value = "手机")
	@TableField("phone")
	private String phone;

	@ApiModelProperty(value = "仓库id")
	@TableField("ware_id")
	private Long wareId;

	@ApiModelProperty(value = "角色名称")
	@TableField(exist = false)
	private String roleName;
}



