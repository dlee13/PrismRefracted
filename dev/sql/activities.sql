SELECT 
  HEX(`world_uuid`) AS worldUuid, 
  `action`,
  `descriptor`,
  `materials`.`material`, 
  `entity_type`, 
  `cause`, 
  HEX(`player_uuid`) AS playerUuid,
  `player`,
  `x`, 
  `y`, 
  `z`, 
  `timestamp`,
  `custom_data`.`data`,
  `custom_data`.`version`
FROM 
  prism_activities AS activities 
  JOIN prism_actions AS actions ON `actions`.`action_id` = `activities`.`action_id` 
  JOIN prism_causes AS causes ON `causes`.`cause_id` = `activities`.`cause_id` 
  JOIN prism_worlds AS worlds ON `worlds`.`world_id` = `activities`.`world_id` 
  LEFT JOIN prism_activities_custom_data AS custom_data ON `custom_data`.`activity_id` = `activities`.`activity_id` 
  LEFT JOIN prism_players AS players ON `players`.`player_id` = `causes`.`player_id` 
  LEFT JOIN prism_entity_types AS entity_types ON `entity_types`.`entity_type_id` = `activities`.`entity_type_id` 
  LEFT JOIN prism_materials AS materials ON `materials`.`material_id` = `activities`.`material_id`
ORDER BY `timestamp` DESC;
