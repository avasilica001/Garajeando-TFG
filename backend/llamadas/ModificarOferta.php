<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdOferta']) and isset($_POST['FechaHoraInicio']) and isset($_POST['FechaHoraFin'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->modificarOferta($_POST['IdOferta'],$_POST['FechaHoraInicio'],$_POST['FechaHoraFin']);
                        if($resultado){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "OK";

                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No se ha podido eliminar la oferta.";
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
