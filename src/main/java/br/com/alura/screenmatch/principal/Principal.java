package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=b1ecfbf6";
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private Scanner input = new Scanner(System.in);
    public void exibeMenu(){
        System.out.println("Digite o nome da série para a busca");
        var nomeSerie  = input.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        		List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1; i <= dados.totalTemporadas(); i++) {
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&Season=" + i  + API_KEY);
			DadosTemporada temp = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(temp);
		}
		temporadas.forEach(System.out::println);

//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodios = temporadas.get(i).episodios();
//            System.out.println("Temporada " + (i+1) +" ################");
//            for (int j = 0; j < episodios.size(); j++) {
//                System.out.println((j+1) + " = " + episodios.get(j).titulo());
//
//            }
//        }

        temporadas.forEach(t ->
                t.episodios().forEach(e ->
                        System.out.println(e.titulo())));
    }

}
