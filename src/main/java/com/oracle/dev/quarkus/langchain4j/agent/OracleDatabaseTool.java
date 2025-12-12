package com.oracle.dev.quarkus.langchain4j.agent;

import java.util.List;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OracleDatabaseTool {

  private ContentRetriever retriever;

  @Inject
  RagInitializationService ragInitializationService;

  @jakarta.annotation.PostConstruct
  public void init() {

    EmbeddingStore<TextSegment> embeddingStore = ragInitializationService.getEmbeddingStore();
    EmbeddingModel embeddingModel = ragInitializationService.getEmbeddingModel();

    if (embeddingStore == null || embeddingModel == null) {
      throw new IllegalStateException("RagInitializationService not properly initialized");
    }

    this.retriever = EmbeddingStoreContentRetriever.builder().embeddingStore(embeddingStore)
        .embeddingModel(embeddingModel).maxResults(10).minScore(0.5).build();
  }

  @Tool("Searches the Oracle Database error codes knowledge base to answer a customer's question about error code descriptions")
  public String searchKnowledgeBase(String query) {
    List<Content> contents = retriever.retrieve(Query.from(query));
    if (contents == null || contents.isEmpty()) {
      return "--- No relevant information found in the Oracle Database knowledge base. ---";
    }
    StringBuilder sb = new StringBuilder();
    for (Content content : contents) {
      TextSegment segment = content.textSegment();
      if (segment != null) {
        sb.append(segment.text()).append('\n');
      }
    }
    return sb.toString();
  }
}
