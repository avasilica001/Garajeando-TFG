<?php
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdCoche']) and isset($_POST['IdComunidad'])){
                        $bbdd = new DBOperations();

                        if($bbdd->anadirCocheExistenteAComunidad($_POST['IdCoche'],$_POST['IdComunidad'])){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = true;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No se ha podido añadir el coche.";
                        }
                }else{
                        $respuesta['error'] = true;
                        $respuesta['mensaje'] = "Faltan campos obligatorios.";
                }
        }else{
                $respuesta['error'] = true;
                $respuesta['mensaje'] = "Petición no válida.";
        }

echo json_encode($respuesta);
?>
