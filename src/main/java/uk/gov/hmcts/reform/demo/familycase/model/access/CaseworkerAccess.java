package uk.gov.hmcts.reform.demo.familycase.model.access;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import uk.gov.hmcts.ccd.sdk.api.HasAccessControl;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.CASE_WORKER;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.CITIZEN;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.DISTRICT_JUDGE;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.LEGAL_ADVISOR;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.SOLICITOR;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.SUPER_USER;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.FAMILY_GENERIC;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRoleConstant.CASE_WORKER;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRoleConstant.CITIZEN;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRoleConstant.DISTRICT_JUDGE;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRoleConstant.LEGAL_ADVISOR;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRoleConstant.SOLICITOR;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRoleConstant.SUPER_USER;

public class CaseworkerAccess implements HasAccessControl {

    @Override
    public SetMultimap<HasRole, Permission> getGrants() {
        SetMultimap<HasRole, Permission> grants = HashMultimap.create();
//        grants.putAll(FAMILY_GENERIC, Permissions.CREATE_READ_UPDATE);
//        grants.putAll(SOLICITOR, Permissions.READ);
//        grants.putAll(SUPER_USER, Permissions.CREATE_READ_UPDATE);
//        grants.putAll(CASE_WORKER, Permissions.CREATE_READ_UPDATE);
//        grants.putAll(LEGAL_ADVISOR, Permissions.CREATE_READ_UPDATE);
//        grants.putAll(DISTRICT_JUDGE, Permissions.CREATE_READ_UPDATE);
        grants.putAll(CITIZEN, Permissions.CREATE_READ_UPDATE);

        return grants;
    }
}

