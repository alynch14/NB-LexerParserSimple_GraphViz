package cosc455101.program1.alynch14;

/*
Course: cosc 455101
Name: Lynch, Adam
 */

/**
 * COSC 455 Programming Languages: Implementation and Design.
 *
 * A Simple Lexical Analyzer Adapted from Sebesta (2010) by Josh Dehlinger
 * further modified by Adam Conover (2012-2018)
 *
 * This syntax analyzer implements a top-down, left-to-right, recursive-descent
 * parser based on the production rules for the simple English language provided
 * by Weber in Section 2.2. Helper methods to get, set and reset the error flag.
 */
/*  BNF:

<S> ::= <NP> <V> <NP> <EOS>
<NP> ::= <A> <AN>
<AN> ::= <ADJ> <N> | <N>                       // Illustrates Choices
<AdVerb> ::= <ADV> <V> | <V>
<ADJ> ::= fast | slow
<A> ::= a | the
<V> ::= loves | hates | eats
<N> ::= dog | cat | rat

<EOS> ::= <the end of the line ends the statement>
 */
public class SyntaxAnalyzer {

	int nodeCount = 0;
	private final LexicalAnalyzer lexer; // The lexer which will provide the tokens

	/**
	 * The constructor initializes the terminal literals in their vectors.
	 */
	public SyntaxAnalyzer(LexicalAnalyzer lexer) {
		this.lexer = lexer;
	}

	/**
	 * Begin analyzing...
	 */
	public void analyze() throws ParseException {
		System.out.println("digraph ParseTree {");
		System.out.printf("\t{\"%s\" [label=\"PARSE TREE\" shape=diamond]};%n", nodeCount);

		start();

		System.out.println("}");

		// Open the default web browser.
		openWebGraphViz();
	}

	/**
	 * Invoke the Start Rule *
	 */
	private void start() throws ParseException {
		sentence(nodeCount);
	}

	// <S> ::= <NP> <V> <NP> <EOS>
	protected void sentence(int from) throws ParseException {
		int node = ++nodeCount;
		log("<S>", from, node);

		NounPhrase(node);
		Verb(node);
		NounPhrase(node);

		EndOfSentance(node);
	}

	// <NP> ::= <A> <AN>
	void NounPhrase(int from) throws ParseException {
		int node = ++nodeCount;
		log("<NP>", from, node);

		Article(node);
		AdjNoun(node);
	}

	// <AN> ::= <ADJ> <N> | <N>
	void AdjNoun(int from) throws ParseException {
		int node = ++nodeCount;
		log("<AN>", from, node);

		if (TOKEN.ADJ == lexer.curToken) {
			Adjective(node);
		}

		Noun(node);
	}

	// This method implements the BNF rule for a verb
	void Verb(int from) throws ParseException {
		log("<V>", from, ++nodeCount, lexer.lexemeBuffer.toString());

		if (TOKEN.VERB != lexer.curToken) {
			raiseException(TOKEN.VERB, from);
		}

		lexer.parseNextToken();
	}

	// This method implements the BNF rule for a noun
	// <N> ::= dog | cat | rat
	void Noun(int from) throws ParseException {
		log("<N>", from, ++nodeCount, lexer.lexemeBuffer.toString());

		if (TOKEN.NOUN != lexer.curToken) {
			raiseException(TOKEN.NOUN, from);
		}

		lexer.parseNextToken();
	}

	// This method implements the BNF rule for an article
	// <A> ::= a | the
	void Article(int from) throws ParseException {
		log("<A>", from, ++nodeCount, lexer.lexemeBuffer.toString());

		if (TOKEN.ARTICLE != lexer.curToken) {
			raiseException(TOKEN.ARTICLE, from);
		}

		lexer.parseNextToken();
	}

	// This method implements the BNF rule for an adjective
	// <A> ::= a | the
	void Adjective(int from) throws ParseException {
		log("<ADJ>", from, ++nodeCount, lexer.lexemeBuffer.toString());

		if (TOKEN.ADJ != lexer.curToken) {
			raiseException(TOKEN.ADJ, from);
		}

		lexer.parseNextToken();
	}

	// End of statement, however it's been defined.
	void EndOfSentance(int from) throws ParseException {
		log("<EOS>", from, ++nodeCount);

		if (TOKEN.EOS != lexer.curToken) {
			raiseException(TOKEN.EOS, from);
		}
	}

	// Show our progress as we go...
	private void log(String bnf, int from, int to) {
		final String t = "\t\"%s\" -> {\"%s\" [label=\"%s\", shape=oval]};%n";
		System.out.printf(t, from, to, bnf);
	}

	private void log(String bnf, int from, int to, String lexeme) {
		log(bnf, from, to);

		final String t = "\t\"%s\" -> {\"%s\" [label=\"%s\", shape=rect]};%n";
		System.out.printf(t, to, to + "_term", lexeme);
	}

	// Handle all of the errors in one place for cleaner parser code.
	private void raiseException(TOKEN expected, int from) throws ParseException {
		final String template = "SYNTAX ERROR: '%s' was expected but '%s' was found.";
		String err = String.format(template, expected.toString(), lexer.lexemeBuffer);

		System.out.printf("\t\"%s\" -> {\"%s\"};%n}%n", from, err);
		throw new ParseException(err);
	}

	/**
	 * To automatically open a browser...
	 */
	private void openWebGraphViz() {
		System.out.println("\nCopy/Paste the above output into: http://www.webgraphviz.com/\n");

		// Automatically open the default browser with the url:
//    try {
//       if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//          Desktop.getDesktop().browse(new URI("http://www.webgraphviz.com/"));
//       }
//    } catch (IOException | URISyntaxException ex) {
//       java.util.logging.Logger.getAnonymousLogger().log(java.util.logging.Level.WARNING, "Could not open browser", ex);
//    }
	}
}