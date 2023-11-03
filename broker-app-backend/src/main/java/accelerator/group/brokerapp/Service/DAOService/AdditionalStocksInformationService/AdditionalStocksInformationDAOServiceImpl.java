package accelerator.group.brokerapp.Service.DAOService.AdditionalStocksInformationService;

import accelerator.group.brokerapp.Entity.AdditionalStocksInformation;
import accelerator.group.brokerapp.Repository.AdditionalStocksInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("AdditionalStocksInformationDAOService")
public class AdditionalStocksInformationDAOServiceImpl implements AdditionalStocksInformationDAOService{

    private final AdditionalStocksInformationRepository additionalStocksInformationRepository;

    @Autowired
    public AdditionalStocksInformationDAOServiceImpl(AdditionalStocksInformationRepository additionalStocksInformationRepository) {
        this.additionalStocksInformationRepository = additionalStocksInformationRepository;
    }

    @Override
    public AdditionalStocksInformation findAddStocksInfoById(Long id) {
        return additionalStocksInformationRepository.findAddStocksInfoById(id);
    }

    @Override
    public void save(AdditionalStocksInformation additionalStocksInformation) {
        additionalStocksInformationRepository.save(additionalStocksInformation);
    }
}
