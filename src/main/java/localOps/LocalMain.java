package localOps;

import ch.qos.logback.classic.Logger;
import data.Empresa;
import data.Empresa.ModeloBeneficio;
import org.slf4j.LoggerFactory;
import utils.EnvUtils;
import utils.LogUtils;

import static data.Field.*;

class LocalMain {
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(LocalMain.class);
	
	public static void main(String[] args) {
		LogUtils.init(LOG);
		LOG.info("Fetching environment variables");
		EnvUtils.init();
		LOG.info("Starting up DatabaseGenerator");
		DatabaseGenerator.init();
		
		LOG.info("Creating ModeloBeneficios");
		ModeloBeneficio norteEuropa = new ModeloBeneficio("Plano de saude Norte Europa")
				.setCampo("Nome", campo_string)
				.setCampo("Data admissao", campo_inteiro)
				.setCampo("Email", campo_string);
		ModeloBeneficio pampulhaIntermedica = new ModeloBeneficio("Plano de saude Pampulha Intermedica")
				.setCampo("Nome", campo_string)
				.setCampo("Data admissao", campo_inteiro)
				.setCampo("Endereco", campo_string);
		ModeloBeneficio dentalSorriso = new ModeloBeneficio("Plano odontologico Dental Sorriso")
				.setCampo("Nome", campo_string)
				.setCampo("Peso(Kg)", campo_decimal)
				.setCampo("Altura(m)", campo_decimal);
		ModeloBeneficio menteSaCorpoSao = new ModeloBeneficio("Plano de saude mental Mente Sa, Corpo Sao")
				.setCampo("Horas meditadas", campo_decimal);
		
		LOG.info("Registering Empresas");
		registraEmpresa(new Empresa("Acme Co", norteEuropa, dentalSorriso));
		registraEmpresa(new Empresa("Tio Patinhas Bank", pampulhaIntermedica, dentalSorriso, menteSaCorpoSao));
		
		LOG.info("Closing database");
		DatabaseGenerator.close();
		
		LOG.info("Finished");
	}
	
	private static void registraEmpresa(Empresa empresa) {
		LOG.info("Registrando " + empresa.nome + ".");
		LOG.info("Resultado: " + (DatabaseGenerator.registerEmpresa(empresa).wasAcknowledged()
				? "Sucesso"
				: "Erro"));
	}
	
}
