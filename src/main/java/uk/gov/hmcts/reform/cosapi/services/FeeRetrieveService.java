package uk.gov.hmcts.reform.cosapi.services;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cosapi.clients.RegisterFeeAPI;
import uk.gov.hmcts.reform.cosapi.config.FeeRetrieveConfiguration;
import uk.gov.hmcts.reform.cosapi.model.FeeApplicationType;
import uk.gov.hmcts.reform.cosapi.model.FeeResponse;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeeRetrieveService {

    private final RegisterFeeAPI registerFeeApi;
    private final FeeRetrieveConfiguration feeRetrieveConfiguration;

    public FeeResponse retrieveFeeDetails(FeeApplicationType feeType) throws Exception {

        FeeRetrieveConfiguration.FeeParameters parameters = feeRetrieveConfiguration.getFeeParametersByFeeType(feeType);
        try {
            log.debug("Making request to Fee Register with parameters : {} ", parameters);

            FeeResponse fee = registerFeeApi.findFee(
                parameters.getChannel(),
                parameters.getEvent(),
                parameters.getJurisdiction1(),
                parameters.getJurisdiction2(),
                parameters.getKeyword(),
                parameters.getService()
            );

            log.debug("Fee response: {} ", fee);

            return fee;
        } catch (FeignException ex) {
            log.error("Fee response error for {}\n\tstatus: {} => message: \"{}\"",
                      parameters, ex.status(), ex.contentUTF8(), ex
            );

            throw new Exception(ex);
        }
    }



}
