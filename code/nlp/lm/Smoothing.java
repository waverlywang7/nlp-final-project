package code.nlp.lm;

public class Smoothing {
    

    public double getNGramProb(NGramModel ngm, NGram ng, double lambda) {
        double ngramCount = ngm.getNGramCount(ng);
        double n_1gramCount = ngm.getN_1GramCount(ng);
        double finalProb = (ngramCount + lambda) / (n_1gramCount + lambda); // times m

        return finalProb;
    }

}
