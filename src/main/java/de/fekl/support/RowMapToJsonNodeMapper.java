package de.fekl.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.fekl.core.MappedRow;
import de.fekl.core.MappedRowImpl;
import de.fekl.core.RowMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RowMapToJsonNodeMapper implements RowMapper<MappedRow<JsonNode>> {

    private final String idColumnName;
    private final ObjectMapper mapper;

    @SneakyThrows
    @Override
    public List<MappedRow<JsonNode>> map(List<Map<String, Object>> rowMapList) {
        var result = new ArrayList<MappedRow<JsonNode>>();
        for (var rowMap : rowMapList) {
            result.add(mapRow(rowMap));
        }
        return result;
    }

    private MappedRow<JsonNode> mapRow(Map<String, Object> rowMap) {
        ObjectNode objectNode = mapper.createObjectNode();
        rowMap.forEach((key, value) -> mapValue(value, objectNode, key));
        return new MappedRowImpl<>(rowMap.get(idColumnName), objectNode);
    }

    private static void mapValue(Object value, ObjectNode objectNode, String column) {
        switch (value) {
            case null -> objectNode.putNull(column);
            case Integer i -> objectNode.put(column, i);
            case String s -> objectNode.put(column, s);
            case Boolean b -> objectNode.put(column, b);
            case Date date -> objectNode.put(column, date.getTime());
            case Long l -> objectNode.put(column, l);
            case Double v -> objectNode.put(column, v);
            case Float v -> objectNode.put(column, v);
            case BigDecimal bigDecimal -> objectNode.put(column, bigDecimal);
            case Byte b -> objectNode.put(column, b);
            case byte[] bytes -> objectNode.put(column, bytes);
            default -> throw new IllegalArgumentException("Unmappable object type: " + value.getClass());
        }
    }
}
