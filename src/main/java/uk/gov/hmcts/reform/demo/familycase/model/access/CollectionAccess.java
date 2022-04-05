package uk.gov.hmcts.reform.demo.familycase.model.access;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import uk.gov.hmcts.ccd.sdk.api.HasAccessControl;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.CASE_WORKER;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.CITIZEN;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.COURT_ADMIN;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.CREATOR;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.DISTRICT_JUDGE;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.LEGAL_ADVISOR;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.SOLICITOR;
import static uk.gov.hmcts.reform.demo.familycase.model.UserRole.SUPER_USER;

public class CollectionAccess implements HasAccessControl {
    @Override
    public SetMultimap<HasRole, Permission> getGrants() {
        SetMultimap<HasRole, Permission> grants = HashMultimap.create();
        grants.putAll(CASE_WORKER, Permissions.READ);
        grants.putAll(COURT_ADMIN, Permissions.READ);
        grants.putAll(SOLICITOR, Permissions.READ);
        grants.putAll(SUPER_USER, Permissions.READ);
        grants.putAll(LEGAL_ADVISOR, Permissions.READ);
        grants.putAll(DISTRICT_JUDGE, Permissions.READ);
        grants.putAll(CREATOR, Permissions.CREATE_READ_UPDATE);
        grants.putAll(CITIZEN, Permissions.CREATE_READ_UPDATE_DELETE);
        //TODO remove delete access for citizen 7th Jan 2022

        return grants;
    }
}

