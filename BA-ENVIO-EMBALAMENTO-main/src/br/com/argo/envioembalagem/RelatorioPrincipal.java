package br.com.argo.envioembalagem;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class RelatorioPrincipal implements AcaoRotinaJava{

	@Override
	public void doAction(ContextoAcao ctx) throws Exception {
		// TODO Auto-generated method stub
		Registro[] linhas = ctx.getLinhas();
		RelatorioEmbalagem  rlm =  new RelatorioEmbalagem();
		boolean RomaneioEncontrado = false;
		List<String> numerosRomaneio = new ArrayList<>();
    try {
    	for (Registro registro : linhas) {
    		
    		BigDecimal nuUnico = (BigDecimal) registro.getCampo("NROUNICO");
//    		 Adiciona o número de romaneio processado à lista
    		numerosRomaneio.add(nuUnico.toString());
    		rlm.buscarRelatorio(ctx, nuUnico, registro);
    		RomaneioEncontrado = true;
    	}
    	if (RomaneioEncontrado) {
			StringBuilder mensagemSucesso = new StringBuilder();
			mensagemSucesso.append("<!DOCTYPE html>").append("<html>").append("<body>")
					.append("<div style='display: flex; align-items: center;'>")
					.append("<img src='https://cdn-icons-png.flaticon.com/256/189/189677.png' style='width:23px; height:23px; margin-right:5px;'>") // Link direto  da  imagem
					.append("<p style='color:SteelBlue; font-family:verdana; font-size:15px; margin: 0;'><b>Relatório de processamento  enviado</b></p>")
					.append("</div>")
					.append("<p style='font-family:courier; color:SteelBlue;'>Os números de Romaneio processados foram: ");
			for (int i = 0; i < numerosRomaneio.size(); i++) {
				mensagemSucesso.append(numerosRomaneio.get(i));
				if (i < numerosRomaneio.size() - 1) {
					mensagemSucesso.append(", ");
				}
			}
			mensagemSucesso.append("</p>").append("</body>").append("</html>");

			ctx.setMensagemRetorno(mensagemSucesso.toString());
		} else {
			StringBuilder mensagemErro = new StringBuilder();
			mensagemErro.append("<!DOCTYPE html>").append("<html>").append("<body>")
					.append("<p style='color:red; font-family:verdana; font-size:15px;'>")
					.append("<img src='https://cdn-icons-png.flaticon.com/512/4201/4201973.png' style='width:23px; height:23px; vertical-align:middle; margin-right:5px;'>") // Link direto  da  imagem
					.append("Erro: Informamos que o envio do Relatorio  Romaneio não foi realizado porque o codigo selecionado não possui um modelo de relatorio personalizado em nosso sistema.")
					.append("</p>").append("</body>").append("</html>");

			ctx.setMensagemRetorno(mensagemErro.toString());
		}
		
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		ctx.setMensagemRetorno("Erro ao executar doAction: " + e.getMessage());
	}
	}

}
