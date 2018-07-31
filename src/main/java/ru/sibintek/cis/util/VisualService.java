package ru.sibintek.cis.util;

import org.apache.solr.common.SolrDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibintek.cis.dao.CommonDao;
import ru.sibintek.cis.dao.converters.SolrDocumentConverter;
import ru.sibintek.cis.model.CommonModel;
import ru.sibintek.cis.model.dto.DrawBubbleChartModel;
import ru.sibintek.cis.model.dto.IsVisualizingData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VisualService {
    private JavaRDD<SolrDocument> resultsRDD = SparkConnector.getInstance().getResultRDD();

    @Autowired
    private CommonDao commonDao;

    @Autowired
    private SolrDocumentConverter converter;

    public List<IsVisualizingData> getVisualizingDataForRoot() {
        List<CommonModel> commonModels = commonDao.getAllIr();
        Set<String> systemsName = new HashSet<>();
        commonModels.forEach(irModel -> systemsName.add(irModel.getIs_name()));
        List<IsVisualizingData> visualizingDataList = new ArrayList<>();
        for (String systemName : systemsName) {
            Function<SolrDocument, Boolean> filter = doc -> (doc.getFieldValue("is_name").equals(systemName));
            JavaRDD<SolrDocument> systemChildrenElement = resultsRDD.filter(filter);
            IsVisualizingData visualizingData = new IsVisualizingData();
            visualizingData.setLabel(systemName);
            visualizingData.setValue(systemChildrenElement.count());
            visualizingData.setUrl("\\is?ISNAME=" + systemName);
            visualizingData.setCaption(systemName);
            visualizingDataList.add(visualizingData);
        }
        return visualizingDataList;
    }

    public List<DrawBubbleChartModel> getVisualizingDataForIs(String isName) {
        List<DrawBubbleChartModel> drawBubbleChartModels = new ArrayList<>();
        Function<SolrDocument, Boolean> filter = doc -> (doc.getFieldValue("object_type").equals("ir") && doc.getFieldValue("is_name").equals(isName));
        JavaRDD<SolrDocument> systemChildrenIr = resultsRDD.filter(filter);
        List<CommonModel> irs = converter.toCommonModel(systemChildrenIr.collect());
        for (CommonModel ir : irs) {
            Function<SolrDocument, Boolean> irFilter = doc -> (doc.getFieldValue("object_type").equals("fu") && doc.getFieldValue("ir_name").equals(ir.getIr_name()));
            JavaRDD<SolrDocument> functions = resultsRDD.filter(irFilter);
            DrawBubbleChartModel model = new DrawBubbleChartModel();
            model.setLabel(ir.getIr_name());
            model.setUrl("ir/?IRNAME=" + ir.getIr_name());
            model.setValue(functions.count());
            drawBubbleChartModels.add(model);
        }
        return drawBubbleChartModels;
    }

}