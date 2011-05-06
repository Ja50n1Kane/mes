/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 0.3.0
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */

package com.qcadoo.mes.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.Restrictions;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.utils.NumberGeneratorService;

@Service
public final class ProductService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    public void generateProductNumber(final ViewDefinitionState state) {
        numberGeneratorService.generateAndInsertNumber(state, "basic", "product", "form", "number");
    }

    public boolean checkIfSubstituteIsNotRemoved(final DataDefinition dataDefinition, final Entity entity) {
        Entity substitute = entity.getBelongsToField("substitute");

        if (substitute == null || substitute.getId() == null) {
            return true;
        }

        Entity substituteEntity = dataDefinitionService.get("basic", "substitute").get(substitute.getId());

        if (substituteEntity == null) {
            entity.addGlobalError("core.message.belongsToNotFound");
            entity.setField("substitute", null);
            return false;
        } else {
            return true;
        }
    }

    public boolean checkSubstituteComponentUniqueness(final DataDefinition dataDefinition, final Entity entity) {
        Entity product = entity.getBelongsToField("product");
        Entity substitute = entity.getBelongsToField("substitute");

        if (substitute == null || product == null) {
            return false;
        }

        SearchResult searchResult = dataDefinition.find()
                .addRestriction(Restrictions.belongsTo(dataDefinition.getField("product"), product.getId()))
                .addRestriction(Restrictions.belongsTo(dataDefinition.getField("substitute"), substitute.getId())).list();

        if (searchResult.getTotalNumberOfEntities() > 0 && !searchResult.getEntities().get(0).getId().equals(entity.getId())) {
            entity.addError(dataDefinition.getField("product"), "basic.validate.global.error.substituteComponentDuplicated");
            return false;
        } else {
            return true;
        }
    }

    public boolean checkIfProductIsNotRemoved(final DataDefinition dataDefinition, final Entity entity) {
        Entity product = entity.getBelongsToField("product");

        if (product == null || product.getId() == null) {
            return true;
        }

        Entity productEntity = dataDefinitionService.get("basic", "product").get(product.getId());

        if (productEntity == null) {
            entity.addGlobalError("core.message.belongsToNotFound");
            entity.setField("product", null);
            return false;
        } else {
            return true;
        }
    }

    public boolean checkIfTechnologyIsNotRemoved(final DataDefinition dataDefinition, final Entity entity) {
        Entity technology = entity.getBelongsToField("technology");

        if (technology == null || technology.getId() == null) {
            return true;
        }

        Entity technologyEntity = dataDefinitionService.get("technologies", "technology").get(technology.getId());

        if (technologyEntity == null) {
            entity.addGlobalError("core.message.belongsToNotFound");
            entity.setField("technology", null);
            return false;
        } else {
            return true;
        }
    }
}