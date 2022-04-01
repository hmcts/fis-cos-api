package uk.gov.hmcts.reform.demo.familycase.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


import static uk.gov.hmcts.reform.demo.familycase.model.UserRoleConstant.FAMILY_GENERIC;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRoleConstant.CASE_WORKER;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRoleConstant.CASE_TYPE_PERMISSIONS_CRU;

@AllArgsConstructor
@Getter
public enum UserRole implements UserRoleConstant{

    FAMILY_GENERIC(UserRoleConstant.FAMILY_GENERIC, UserRoleConstant.CASE_TYPE_PERMISSIONS_CRU);
    CASE_WORKER(UserRoleConstant.CASE_WORKER, UserRoleConstant.CASE_TYPE_PERMISSIONS_CRU),
    COURT_ADMIN(UserRoleConstant.COURT_ADMIN, UserRoleConstant.CASE_TYPE_PERMISSIONS_CRU),
    LEGAL_ADVISOR(UserRoleConstant.LEGAL_ADVISOR UserRoleConstant.CASE_TYPE_PERMISSIONS_CRU),
    DISTRICT_JUDGE(UserRoleConstant.DISTRICT_JUDGE, UserRoleConstant.CASE_TYPE_PERMISSIONS_CRU),
    SUPER_USER(UserRoleConstant.SUPER_USER, UserRoleConstant.CASE_TYPE_PERMISSIONS_CRU),
    SOLICITOR(UserRoleConstant.SOLICITOR, UserRoleConstant.CASE_TYPE_PERMISSIONS_CRU),
    CITIZEN(UserRoleConstant.CITIZEN, UserRoleConstant.CASE_TYPE_PERMISSIONS_CRU),
    CREATOR(UserRoleConstant.CREATOR, UserRoleConstant.CASE_TYPE_PERMISSIONS_CRUD);


    UserRole(String role, String caseTypePermissions) {
    }


//    @JsonValue
//    private final String role;
//    private final String caseTypePermissions;

}
