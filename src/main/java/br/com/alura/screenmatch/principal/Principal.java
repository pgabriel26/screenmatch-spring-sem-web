package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        temporadas.forEach(t -> t.episodios().forEach(e ->  System.out.println(e.titulo())));

        List<DadosEpisodio> todosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());


        System.out.println("\nMelhores episodios!\n");
        todosEpisodios.stream()
                .filter(e -> !e.avaliacao().equals("N/A"))
//                .peek(e -> System.out.println("primeiro filtro(N/A) " + e))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenaçcao " + e))
                .limit(10)
//                .peek(e -> System.out.println("Limite "+ e))
                .map(e -> e.episodio() + " " + e.titulo().toUpperCase())
//                .peek(e -> System.out.println("map "+ e))
                .forEach(System.out::println);
        System.out.println("\n");
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numeroTemporada(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);


//        System.out.println("digite um trecho do episodio que vc deseja buscar");
//        var trechoTitulo = input.nextLine();
//
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if(episodioBuscado.isPresent()) {
//            System.out.println("Episodio encontrado!\n");
//            System.out.println("temporada: " + episodioBuscado.get().getTemporada());
//        } else {
//            System.out.println("episodio nao encontrado");
//        }
//
//        System.out.println("A partir de que ano voce deseja ver os episodios?");
//        var ano = input.nextInt();
//        input.nextLine();
//
//        LocalDate dataDeBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataDeBusca))
//                .forEach(e -> System.out.println(
//                        "Teporada: " + e.getTemporada() +
//                                " Episodio: " + e.getTitulo() +
//                                " Data lancamento: " + e.getDataLancamento().format(formatador)
//                ));


        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);
    }
}
