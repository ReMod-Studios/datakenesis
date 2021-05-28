package com.remodstudios.datakenesis

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class VillagerTrades(
    val tiers: List<Tier>
)

@Serializable
data class Tier(
    val trades: List<Trade>
)

@Serializable
data class Trade(
    val wants: List<Want>,
    val gives: List<Give>
)

@Serializable
data class Want(
    val item: String,
    val quantity: NumberProviderInt
)

@Serializable
data class Give(
    val item: String,
    val functions: List<ItemModifier> = listOf()
)

const val TEXT = """
{
  "tiers": [
    {
      "trades": [
        {
          "wants": [
            {
              "item": "minecraft:coal:0",
              "quantity": {
                "min": 16,
                "max": 24
              }
            }
          ],
          "gives": [
            {
              "item": "minecraft:emerald"
            }
          ]
        },
        {
          "wants": [
            {
              "item": "minecraft:emerald",
              "quantity": {
                "min": 4,
                "max": 6
              }
            }
          ],
          "gives": [
            {
              "item": "minecraft:iron_helmet"
            }
          ]
        }
      ]
    },
    {
      "trades":[
      {
        "wants": [
          {
            "item": "minecraft:iron_ingot",
            "quantity": {
              "min": 7,
              "max": 9
            }
          }
        ],
        "gives": [
          {
            "item": "minecraft:emerald",
            "functions": [
              {
                "function": "explosion_decay"
              }
            ]
          }
        ]
      },
      {
        "wants": [
          {
            "item": "minecraft:emerald",
            "quantity": {
              "min": 10,
              "max": 14
            }
          }
        ],
        "gives": [
          {
            "item": "minecraft:iron_chestplate"
          }
        ]
      }
      ]
    },
    {
      "trades": [
        {
          "wants": [
            {
              "item": "minecraft:diamond",
              "quantity": {
                "min": 3,
                "max": 4
              }
            }
          ],
          "gives": [
            {
              "item": "minecraft:emerald"
            }
          ]
        },
        {
          "wants": [
            {
              "item": "minecraft:emerald",
              "quantity": {
                "min": 16,
                "max": 19
              }
            }
          ],
          "gives": [
            {
              "item": "minecraft:diamond_chestplate",
              "functions": [
                {
                  "function": "enchant_with_levels",
                  "treasure": false,
                  "levels": {
                    "min": 5,
                    "max": 19
                  }
                }
              ]
            }
          ]
        }
      ]
    },
    {
      "trades": [
        {
          "wants": [
            {
              "item": "minecraft:emerald",
              "quantity": {
                "min": 5,
                "max": 7
              }
            }
          ],
          "gives": [
            {
              "item": "minecraft:chainmail_boots"
            }
          ]
        },
        {
          "wants": [
            {
              "item": "minecraft:emerald",
              "quantity": {
                "min": 9,
                "max": 11
              }
            }
          ],
          "gives": [
            {
              "item": "minecraft:chainmail_leggings"
            }
          ]
        },
        {
          "wants": [
            {
              "item": "minecraft:emerald",
              "quantity": {
                "min": 5,
                "max": 7
              }
            }
          ],
          "gives": [
            {
              "item": "minecraft:chainmail_helmet"
            }
          ]
        },
        {
          "wants": [
            {
              "item": "minecraft:emerald",
              "quantity": {
                "min": 11,
                "max": 15
              }
            }
          ],
          "gives": [
            {
              "item": "minecraft:chainmail_chestplate"
            }
          ]
        }
      ]
    }
  ]
}
"""

fun main() {
    val trades = Json.decodeFromString<VillagerTrades>(TEXT)
    println(trades)
}