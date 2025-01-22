<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdComunidad'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->actualizarCodigoInvitacion($_POST['IdComunidad']);
                        $respuesta['error'] = false;
                        $respuesta['mensaje'] = "OK";
			$respuesta['CodInvitacion'] = $resultado;
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
