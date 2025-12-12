package com.oracle.dev.quarkus.langchain4j.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.oracle.CreateOption;
import dev.langchain4j.store.embedding.oracle.OracleEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RagInitializationService {

  @Inject
  EmbeddingModel embeddingModel;

  @Inject
  DataSource dataSource;

  private EmbeddingStore<TextSegment> embeddingStore;

  @PostConstruct
  public void initialize() throws SQLException {
    embeddingStore = OracleEmbeddingStore.builder().dataSource(dataSource)
        .embeddingTable("rag_agent_embeddings", CreateOption.CREATE_IF_NOT_EXISTS).build();
    ingestOracleErrorCodes();
  }

  public EmbeddingStore<TextSegment> getEmbeddingStore() {
    return embeddingStore;
  }

  public EmbeddingModel getEmbeddingModel() {
    return embeddingModel;
  }

  private void ingestOracleErrorCodes() {
    List<Document> documents = new ArrayList<>();

    try (var inputStream = RagInitializationService.class.getClassLoader()
        .getResourceAsStream("rag/oracle-error-codes-descriptions.md")) {

      if (inputStream == null) {
        throw new IllegalStateException(
            "Resource rag/oracle-error-codes-descriptions.md not found in src/main/resources/rag/");
      }

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        documents = reader.lines().filter(line -> !line.trim().isEmpty()).map(Document::from)
            .collect(Collectors.toList());
      }

    } catch (IOException e) {
      throw new RuntimeException("Failed to load oracle-error-codes-descriptions.md", e);
    }

    if (embeddingModel != null) {
      for (Document doc : documents) {
        Embedding embedding = embeddingModel.embed(doc.text()).content();
        embeddingStore.add(embedding, TextSegment.from(doc.text()));
      }
    }

    System.out.println(
        "--- Loaded " + documents.size() + " Oracle AI Database 26ai error code entries into the vector store. ---");
  }
}
