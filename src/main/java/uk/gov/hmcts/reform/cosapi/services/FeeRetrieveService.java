package uk.gov.hmcts.reform.cosapi.services;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cosapi.clients.RegisterFeeAPI;
import uk.gov.hmcts.reform.cosapi.config.FeeRetrieveConfiguration;
import uk.gov.hmcts.reform.cosapi.exception.FeeCodeNotFoundException;
import uk.gov.hmcts.reform.cosapi.model.FeeResponse;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeeRetrieveService {

    private final RegisterFeeAPI registerFeeApi;
    private final FeeRetrieveConfiguration feeRetrieveConfiguration;

    public FeeResponse retrieveFeeDetails(String caseType) throws FeeCodeNotFoundException {

        Optional<FeeRetrieveConfiguration.FeeParameters> feeParametersByFeeType = Optional.ofNullable(
            feeRetrieveConfiguration.getFeeParametersByFeeType(caseType));
        feeParametersByFeeType.orElseThrow(() -> new FeeCodeNotFoundException("Fee Code is not Valid"));
        log.debug("Making request to Fee Register with parameters : {} ", feeParametersByFeeType.get());
        if (feeParametersByFeeType.isPresent()) {
            FeeResponse fee = registerFeeApi.findFee(
                feeParametersByFeeType.get().getChannel(),
                feeParametersByFeeType.get().getEvent(),
                feeParametersByFeeType.get().getJurisdiction1(),
                feeParametersByFeeType.get().getJurisdiction2(),
                feeParametersByFeeType.get().getKeyword(),
                feeParametersByFeeType.get().getService()
               );

            log.debug("Fee response: {} ", fee);

            return fee;
        }
        return null;
    }



}
