package br.com.argo.envioembalagem;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.sankhya.util.SessionFile;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper.ParametroRelatorio;
import br.com.sankhya.ws.ServiceContext;

public class RelatorioEmbalagem {
	
	public void buscarRelatorio(ContextoAcao ctx, BigDecimal nuanexo, Registro registro) {
	    ModeloEmail emailsAnexos = new ModeloEmail();
	    BigDecimal nuRfromaneio = new BigDecimal(241); // relatorio maritmo

	    JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
	    SessionHandle hnd = JapeSession.open();
	    NativeSql nativeSql = new NativeSql(jdbc);
	    ResultSet rset = null;
	    List<Object> lstParam = new ArrayList<Object>();
	    byte[] BytesRomaneio = null;
//	    String emailParam = (String) ctx.getParam("EMAILADC");
//	    String[] emails = emailParam.trim().split("\\s*,\\s*");
	    String parceiro = null;
	    String variedade = null;
	    String emailParc = null;
	    int codUsu = ctx.getUsuarioLogado().intValue();
	    String emailUser = null;
	    BigDecimal nuUnico = (BigDecimal) registro.getCampo("NROUNICO");
	    Date Dtcolheita = (Date) registro.getCampo("DTCOLHEITA");
	    // Formatar a data de colheita
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    String dtColheitaFormatada = dateFormat.format(Dtcolheita);
	    try {
	    	jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
            ResultSet query = nativeSql.executeQuery("SELECT ENT.NROUNICO, PAR.NOMEPARC, GRU.AD_DESCRESUMO,PAR.EMAILNFE  \r\n"
            		+ "                    FROM AD_ROMANEIOENTR ENT  \r\n"
            		+ "                    JOIN TGFPAR PAR ON ENT.CODPARC = PAR.CODPARC  \r\n"
            		+ "                    JOIN TGFPRO PRO ON ENT.CODPROD = PRO.CODPROD  \r\n"
            		+ "                    JOIN TGFGRU GRU ON PRO.CODGRUPOPROD = GRU.CODGRUPOPROD  \r\n"
            		+ "                    WHERE ENT.NROUNICO = " + nuUnico);
            while (query.next()) {
            	parceiro = query.getString("NOMEPARC");
            	variedade = query.getString("AD_DESCRESUMO");
            	emailParc = query.getString("EMAILNFE");
            }
            ResultSet query2 = nativeSql.executeQuery("SELECT AD_GRUPOMAIL FROM TSIUSU WHERE CODUSU = " + codUsu);
            while (query2.next()) {
            	emailUser = query2.getString("AD_GRUPOMAIL");
            }
	    	
	    	
	        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
	        ParametroRelatorio pk = new ParametroRelatorio("PK_NROUNICO", BigDecimal.class.getName(), nuanexo);
	        lstParam.add(pk);
	        // Gere o relatório principal (embalagem)
	        BytesRomaneio = AgendamentoRelatorioHelper.getPrintableReport(nuRfromaneio, lstParam, ctx.getUsuarioLogado(), dwfFacade);

	        SessionFile sessionFileRomaneio = SessionFile.createSessionFile("Relatorio.pdf", "Relatorio", BytesRomaneio);
	        // Adicione os SessionFile na sessão
	        ServiceContext.getCurrent().putHttpSessionAttribute("Romaneio", sessionFileRomaneio);
	        // Construindo o assunto do email
	        String assunto = "Relatório de processamento ";
	        String saudacao = obterSaudacao();
	        String mensagem = "<!DOCTYPE html>\r\n"
	                + "<html>\r\n"
	                + "<head>\r\n"
	                + "    <meta charset=\"utf-8\"/>\r\n"
	                + "    <title>Email</title>\r\n"
	                + "    <style>\r\n"
	                + "        table {\r\n"
	                + "            border-collapse: collapse;\r\n"
	                + "            width: 100%;\r\n"
	                + "        }\r\n"
	                + "        th, td {\r\n"
	                + "            border: 1px solid black;\r\n"
	                + "            padding: 8px;\r\n"
	                + "            text-align: left;\r\n"
	                + "        }\r\n"
	                + "    </style>\r\n"
	                + "</head>\r\n"
	                + "<body>\r\n"
	                + "    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"star1\">\r\n"
	                + "        <tr>\r\n"
	                + "            <td align=\"center\" style=\"background-color:#1e6533;\">\r\n"
	                + "                <div class=\"image-container\">\r\n"
	                + "                    <img border=\"0\" style=\"width:17%;\" src=\"https://argofruta.com/wp-content/uploads/2021/05/Logo-text-green.png\" alt=\"\">\r\n"
	                + "                </div>\r\n"
	                + "            </td>\r\n"
	                + "        </tr>\r\n"
	                + "    </table>\r\n"
	                + "    <span>\r\n"
	                + "        "+saudacao+",<br/><br/>\r\n"
	                + "        Segue em anexo, o relatório de processamento do dia :  " +dtColheitaFormatada+ "<br/><br/>\r\n"
	                + "        Produtor: " + parceiro + "<br/>\r\n"
	                + "        Variedade: " + variedade + "<br/><br/>\r\n"
	                + "        <span style=\"color:red;\">\r\n"
	                + "            Obs.: Por favor, não responda a este e-mail.<br/>\r\n"
	                + "            Qualquer dúvida, procure o comprador ou o(a) fiscal responsável.<br/>\r\n"
	                + "        </span><br/>\r\n"
	                + "        Atenciosamente;\r\n"
	                + "    </span>\r\n"
	                + "</body>\r\n"
	                + "</html>\r\n";
	        
	        if (emailParc != null && !emailParc.isEmpty()) {
	            String[] emails = emailParc.split("\\s*;\\s*");
	            for (String email : emails) {
	                emailsAnexos.enviarEmailComAnexos(dwfFacade, ctx, BytesRomaneio, email, assunto, mensagem, emailUser);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        ctx.setMensagemRetorno("Erro ao executar buscarRelatorio: " + e.getMessage());
	    } finally {
	        JapeSession.close(hnd);
	        JdbcWrapper.closeSession(jdbc);
	        NativeSql.releaseResources(nativeSql);
	    }
	}
	public  String obterSaudacao() {
		Calendar agora = Calendar.getInstance();
		 int hora = agora.get(Calendar.HOUR_OF_DAY);

        if (hora >= 0 && hora < 12) {
            return "Bom dia";
        } else if (hora >= 12 && hora < 18) {
            return "Boa tarde";
        } else {
            return "Boa noite";
        }
    }


}
