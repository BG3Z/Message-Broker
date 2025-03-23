#!/bin/bash

java sdis.broker.client.unit.Auth localhost cllamas qwerty
java sdis.broker.client.unit.AddMsg localhost 1 maricon
read -p "Pulsa una tecla para salir"
