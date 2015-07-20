package org.mifosplatform.mpesa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifosplatform.mpesa.configuration.MpesaConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MpesaConfiguration.class)
@WebAppConfiguration
public class MifosxMpesaApplicationTests {

	@Test
	public void contextLoads() {
	}

}
