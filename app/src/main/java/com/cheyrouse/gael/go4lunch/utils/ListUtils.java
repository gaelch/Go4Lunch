package com.cheyrouse.gael.go4lunch.utils;

import com.cheyrouse.gael.go4lunch.models.Prediction;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static List<ResultDetail> transforPredictionToResultDetail(List<ResultDetail> resultDetailList, List<Prediction> resultsPredictions){
        List<ResultDetail> newResults = new ArrayList<>();
        if (resultDetailList.size() > 0) {
            if (resultsPredictions != null && resultsPredictions.size() > 0) {
                for (ResultDetail resultDetail : resultDetailList) {
                    for (Prediction prediction : resultsPredictions) {
                        if (resultDetail.getId().equals(prediction.getId())) {
                            newResults.add(resultDetail);
                        }
                    }
                }
            }
        }
        return newResults;
    }
}
