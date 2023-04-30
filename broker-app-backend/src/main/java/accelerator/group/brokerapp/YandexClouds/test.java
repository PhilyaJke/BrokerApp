package accelerator.group.brokerapp.YandexClouds;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Optional;

public class test{

    SecuritiesRepository securitiesRepository;

    @Autowired
    public test(SecuritiesRepository securitiesRepository) {
        this.securitiesRepository = securitiesRepository;
    }

    public void addIconsPaths(int count) throws InterruptedException {
        AWSCredentials awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return "YCAJENtodKHlJrII100dGkrEX";
            }

            @Override
            public String getAWSSecretKey() {
                return "YCP7qnLj-gFCUkvWK9TukZiZXKwpwsoUf_9FQ4iY";
            }
        };

        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net","ru-central1"
                        )
                )
                .build();


        var listFiles = new File("/Users/philyaborozdin/Desktop/icons").listFiles();
        for(int i = count; i < listFiles.length; i++){
//            try {
                var s = s3.getObject("securitiesicons", listFiles[i].getName());
                Optional<Securities> securities = securitiesRepository.findByTicker(listFiles[i].getName().substring(0, listFiles[i].getName().length()-4));
                if(s.equals(null) || securities.isEmpty()){
                    continue;
                }else {
                    System.out.println(s.getObjectContent().getHttpRequest().getURI().toString());
                    securities.get().setIconPath(s.getObjectContent().getHttpRequest().getURI().toString());
                    securitiesRepository.save(securities.get());
                    count++;
                }
//            }catch (SdkClientException exc){
//                addIconsPaths(count);
//            }
        }
    }
}
