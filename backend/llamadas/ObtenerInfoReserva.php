<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='GET'){
                if (isset($_GET['IdCoche']) and isset($_GET['IdReserva'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->obtenerCoche($_GET['IdCoche']);
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "OK";
                                $respuesta['Coche'] = $resultado;

                                $resultado2 = $bbdd->obtenerFotosCoche($_GET['IdCoche']);
                                $respuesta['Fotos'] = $resultado2;

                                $resultado3 = $bbdd->obtenerInfoReserva($_GET['IdReserva']);
                                $respuesta['Reserva'] = $resultado3;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No hay fotos para mostrar.";
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
