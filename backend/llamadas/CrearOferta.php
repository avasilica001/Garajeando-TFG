<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdCoche']) and isset($_POST['IdComunidad']) and isset($_POST['FechaHoraInicio']) and isset($_POST['FechaHoraFin'])){
                        $bbdd = new DBOperations();
                        
                        if($bbdd->crearOferta($_POST['IdCoche'],$_POST['IdComunidad'],$_POST['FechaHoraInicio'],$_POST['FechaHoraFin'])){
                                $respuesta['error'] = false;
                        	$respuesta['mensaje'] = "La oferta se ha creado correctamente.";
                        }else{
				$respuesta['error'] = true;
                        	$respuesta['mensaje'] = "Ya hay una oferta dentro de ese rango de fechas.";
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
