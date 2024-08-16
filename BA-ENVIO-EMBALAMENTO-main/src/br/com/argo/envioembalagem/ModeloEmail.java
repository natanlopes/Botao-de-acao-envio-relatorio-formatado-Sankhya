package br.com.argo.envioembalagem;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;

public class ModeloEmail {
	String msg;

	public void enviarEmailComAnexos(EntityFacade dwfEntityFacade, ContextoAcao contexto, byte[] bytesRomaneio,
			String emailParc, String assunto, String mensagem, String emailuser) throws Exception {
		BigDecimal codFila = null;
		BigDecimal nuAnexoInvoice = null;
		BigDecimal seq = null;
		JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();

		NativeSql nativeSql = new NativeSql(jdbc);

		try {
			// Passo 1: Criação da Mensagem na MSDFilaMensagem para obter o CODFILA
			DynamicVO dynamicVO1 = (DynamicVO) dwfEntityFacade.getDefaultValueObjectInstance("MSDFilaMensagem");
			dynamicVO1.setProperty("ASSUNTO", assunto);
			dynamicVO1.setProperty("CODMSG", null);
			dynamicVO1.setProperty("DTENTRADA", TimeUtils.getNow());
			dynamicVO1.setProperty("STATUS", "Pendente");
			dynamicVO1.setProperty("CODCON", BigDecimal.ZERO);
			dynamicVO1.setProperty("TENTENVIO", BigDecimalUtil.valueOf(3));
			dynamicVO1.setProperty("MENSAGEM", mensagem.toCharArray());
			dynamicVO1.setProperty("TIPOENVIO", "E");
			dynamicVO1.setProperty("MAXTENTENVIO", BigDecimalUtil.valueOf(3));
			dynamicVO1.setProperty("EMAIL", emailParc.trim());
			dynamicVO1.setProperty("CODSMTP", null);
			dynamicVO1.setProperty("CODUSUREMET", contexto.getUsuarioLogado());
			dynamicVO1.setProperty("MIMETYPE", "text/html");
			PersistentLocalEntity createEntity = dwfEntityFacade.createEntity("MSDFilaMensagem", (EntityVO) dynamicVO1);
			codFila = ((DynamicVO) createEntity.getValueObject()).asBigDecimal("CODFILA");
			// Passo 2: Criação dos Anexos na AnexoMensagem para obter os NUANEXO
			// Anexo Invoice
			DynamicVO dynamicVO2Invoice = (DynamicVO) dwfEntityFacade.getDefaultValueObjectInstance("AnexoMensagem");
			dynamicVO2Invoice.setProperty("ANEXO", bytesRomaneio);
			dynamicVO2Invoice.setProperty("NOMEARQUIVO", "Relatorio_processamento.pdf");
			dynamicVO2Invoice.setProperty("TIPO", "application/pdf");
			createEntity = dwfEntityFacade.createEntity("AnexoMensagem", (EntityVO) dynamicVO2Invoice);
			nuAnexoInvoice = ((DynamicVO) createEntity.getValueObject()).asBigDecimal("NUANEXO");

			// Passo 3: Associação dos Anexos à Mensagem na TMDAXM
			// Anexo Invoice
			DynamicVO dynamicVO3Invoice = (DynamicVO) dwfEntityFacade.getDefaultValueObjectInstance("AnexoPorMensagem");
			dynamicVO3Invoice.setProperty("CODFILA", codFila);
			dynamicVO3Invoice.setProperty("NUANEXO", nuAnexoInvoice);
			dwfEntityFacade.createEntity("AnexoPorMensagem", (EntityVO) dynamicVO3Invoice);
			
			String querySeq  = ("SELECT MAX(SEQUENCIA) + 1 AS SEQUENCIA FROM TMDFMD WHERE CODFILA = " + codFila);
			ResultSet rsSeq = nativeSql.executeQuery(querySeq);
			
			while (rsSeq.next()) {
				seq = rsSeq.getBigDecimal("SEQUENCIA");
			}
			DynamicVO dynamicVO4EmailCopia = (DynamicVO) dwfEntityFacade.getDefaultValueObjectInstance("MSDDestFilaMensagem");
			dynamicVO4EmailCopia.setProperty("CODFILA", codFila);
			dynamicVO4EmailCopia.setProperty("EMAIL", emailuser.trim());
			dynamicVO4EmailCopia.setProperty("SEQUENCIA", seq);
			dwfEntityFacade.createEntity("MSDDestFilaMensagem", (EntityVO) dynamicVO4EmailCopia);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Erro na criação da mensagem: " + e.getMessage();
			contexto.setMensagemRetorno(msg);
		} finally {
			JdbcWrapper.closeSession(jdbc);
			NativeSql.releaseResources(nativeSql);
		}
	}

}
