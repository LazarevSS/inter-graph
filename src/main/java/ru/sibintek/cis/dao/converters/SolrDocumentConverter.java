package ru.sibintek.cis.dao.converters;

import org.apache.solr.common.SolrDocument;
import ru.sibintek.cis.model.FunctionModel;
import ru.sibintek.cis.model.IrModel;
import ru.sibintek.cis.model.IsModel;
import ru.sibintek.cis.model.dto.IsVisualizingData;

import java.util.List;

public interface SolrDocumentConverter {
    IsVisualizingData toIsVisualizingData(SolrDocument document);

    List<IsVisualizingData> toIsVisualizingData(List<SolrDocument> documents);

    IsModel toIsModel(SolrDocument document);

    List<IsModel> toIsModel(List<SolrDocument> documents);

    IrModel toIrModel(SolrDocument document);

    List<IrModel> toIrModel(List<SolrDocument> documents);

    FunctionModel toFunctionModel(SolrDocument document);

    List<FunctionModel> toFunctionModel(List<SolrDocument> documents);
}
