package com.youxing.car.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**   
 * @author mars   
 * @date 2017年4月25日 上午9:06:42 
 */
public class SysLog {
	private Long id;

    private String loginName;

    private Long uid;

    private String optContent;
    
    private String description ;

    private String clientIp;
    
    private String clientType ;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName == null ? null : loginName.trim();
    }

    public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getOptContent() {
        return optContent;
    }

    public void setOptContent(String optContent) {
        this.optContent = optContent == null ? null : optContent.trim();
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp == null ? null : clientIp.trim();
    }

   
    public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
	 this.description = description == null ? null : description.trim();
		
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType == null ? null : clientType.trim();
	}
	
	
}


