package scut.deng.didservice.pojo.issue;


import lombok.Data;

import java.time.LocalDate;

@Data
public class PredefinedMaterials {
    public String name;                   // 姓名
    public String gender        ;                 // 性别
    public String maritalStatus           ;   // 婚姻状况
    LocalDate birthday;        // 生日
    public String placeOfBirth;    // 出生地
    public String mobilePhone;    // 电话
    public String workPhone;     // 工作电话
    public String PpersonalEmail;     // 个人邮箱
    public String workEmail;         // 工作邮箱
    public String highestEducationDegree;// 最高学历
    public String companyAddress;       // 公司地址
    public String companyName;   // 公司名称
    public String homeAddress;          // 家庭地址
    public String addressLine;     // 详细地址
    public String city;          // 城市
    public String country;            // 国家
    public String driverLicense;      // 驾驶证ID
    public String passportID;         // 护照号码
    public String socialSecurityNumber;   // 社保号
}
